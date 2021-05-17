package de.disk0.ldap.impl.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.disk0.dbutil.api.exceptions.SqlException;
import de.disk0.ldap.api.entities.Complaint;
import de.disk0.ldap.api.entities.EntryAcl;
import de.disk0.ldap.api.entities.EntryType;
import de.disk0.ldap.api.entities.LdapEntry;
import de.disk0.ldap.api.entities.LdapPermission;
import de.disk0.ldap.api.entities.Membership;
import de.disk0.ldap.api.entities.ResetToken;
import de.disk0.ldap.api.entities.query.EntryQuery;
import de.disk0.ldap.api.entities.session.SessionHolder;
import de.disk0.ldap.api.entities.session.User;
import de.disk0.ldap.api.exceptions.AuthException;
import de.disk0.ldap.api.exceptions.AuthFailedException;
import de.disk0.ldap.api.exceptions.LdapException;
import de.disk0.ldap.api.exceptions.NotAuthenticatedException;
import de.disk0.ldap.api.exceptions.NotAuthorizedException;
import de.disk0.ldap.api.services.LdapRepository;
import de.disk0.ldap.api.services.LdapService;
import de.disk0.ldap.api.utils.AuthUtils;
import de.disk0.ldap.api.utils.PasswordGenerator;
import de.disk0.ldap.api.utils.TokenGenerator;
import de.disk0.ldap.impl.services.repos.EntryAclRepo;
import de.disk0.ldap.impl.services.repos.EntryRepo;
import de.disk0.ldap.impl.services.repos.MembershipRepo;
import de.disk0.ldap.impl.services.repos.TokenRepo;
import de.disk0.ldap.mail.api.MailService;


@Service
@Order(value = Ordered.LOWEST_PRECEDENCE)
public class LdapServiceImpl implements LdapService {

	private static final Log log = LogFactory.getLog(LdapServiceImpl.class);

	@Autowired
	private LdapRepository ldapRepository;
	
	@Autowired
	private EntryRepo entryRepo;
	
	@Autowired
	private EntryAclRepo entryAclRepo;
	
	@Autowired
	private MembershipRepo membershipRepo;
	
	@Autowired
	private TokenRepo tokenRepo;
	
	@Autowired
	private MailService mailService;
	
	private LdapEntry root;
	
	private LdapEntry getWithPermission(String id, LdapPermission... permission) throws LdapException {
		try {
			if(AuthUtils.isAdmin()) {
				return entryRepo.get(id);
			} else {
				EntryQuery eq = new EntryQuery();
				eq.setPermissions(Arrays.asList(permission));
				eq.setPrincipalIds(SessionHolder.get().getPrincipalIds());
				eq.setEntryId(id);
				return entryRepo.findOne(eq);
			}
		} catch (Exception e) {
			throw new LdapException("LDAP.GET_FAILED", e.getMessage(), e);
		}
	}
	
	private void checkPermission(String id, LdapPermission... permissions) throws AuthException {
		AuthUtils.checkLoggedIn();
		try {
			log.info("checking permissions: "+StringUtils.join(permissions,", "));
			LdapEntry e = getWithPermission(id, permissions);
			if(e!=null) {
				log.info("checking permissions: OK!");
				return;
			}
		} catch (Exception e) {
			log.info("checking permissions: FAILED!",e);
		}
		log.info("checking permissions: NO!");
		throw new NotAuthorizedException();
	}
	
	
	@Scheduled(initialDelay = 15000, fixedDelay = 60000)
	public void updateInternal() throws InvalidNameException, JsonProcessingException, SqlException, AuthException {
		try {
			this.root = ldapRepository.getRoot();
			entryRepo.save(this.root);
			Date d = DateUtils.addSeconds(new Date(), -10);
			recursive(this.root);
			log.info("finished update, removing obsolete ... " );
			entryRepo.deleteObsolete(d);
			membershipRepo.deleteObsolete(d);
		} catch (Exception e) {
			log.error("error updating from LDAP: ",e);
		}
	}
	
	private void recursive(LdapEntry e) throws LdapException, SqlException {
		List<LdapEntry> ecs = ldapRepository.getChildren(e); 
		for(LdapEntry ec : ecs) {
			ec.setParentId(e.getId());
			ec = entryRepo.save(ec);
			if(ec.getType()==EntryType.UNIT) {
				recursive(ec);
			} else {
				for(LdapEntry memberOf : ldapRepository.getMemberships(ec.getId())) {
					Membership m = new Membership();
					m.setPrincipalId(ec.getId());
					m.setGroupId(memberOf.getId());
					membershipRepo.save(m);
				}
			} 
			continue;
		}
	}

	private boolean checkCharacters(String name) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9]+[a-zA-Z0-9-_\\.]*[a-zA-Z0-9]+$");
		Matcher matcher = p.matcher(name);
		return matcher.matches();
	}
	
	
	@Override
	public List<Complaint> checkCreate(
			String parentId, EntryType type, 
			String name, 
			String givenname, 
			String familyname, 
			String displayname, 
			String email, 
			String description, 
			boolean notify) throws SqlException, LdapException {
		List<Complaint> out = new ArrayList<Complaint>();
		
		LdapEntry e = getWithPermission(parentId, LdapPermission.CREATE);
		if(e == null) {
			out.add(new Complaint("LDAP.CREATE.NOT_AUTHORIZED"));
			return out;
		} 
		
		
		if(type == null) {
			out.add(new Complaint("LDAP.CREATE.TYPE_REQUIRED"));
			return out;
		} 
		if(name.length() < 2) {
			out.add(new Complaint("LDAP.CREATE.NAME_TOO_SHORT"));
		} else if(!checkCharacters(name)) {
			out.add(new Complaint("LDAP.CREATE.NAME_CONTAINS_INVALID_CHARS"));
		} else if(!entryRepo.checkName(name)) {
			out.add(new Complaint("LDAP.CREATE.NAME_TAKEN"));
		}
		if (type == EntryType.USER) {
			if(StringUtils.isEmpty(email)) {
				
			} else if(!EmailValidator.getInstance().isValid(email)) {
				out.add(new Complaint("LDAP.CREATE.INVALID_EMAIL"));
			}
			if(StringUtils.isEmpty(givenname)) {
				out.add(new Complaint("LDAP.CREATE.GIVENNAME_REQUIRED"));
			}
			if(StringUtils.isEmpty(familyname)) {
				out.add(new Complaint("LDAP.CREATE.FAMILYNAME_REQUIRED"));
			}
			if(StringUtils.isEmpty(displayname)) {
				out.add(new Complaint("LDAP.CREATE.DISPLAYNAME_REQUIRED"));
			}
		}
		return out;
	}

	@Override
	public LdapEntry create(
			String parentId, EntryType type, 
			String name, 
			String givenname, 
			String familyname, 
			String displayname, 
			String email, 
			String description, 
			boolean notify) throws LdapException, AuthException, InvalidNameException, SqlException {
		checkPermission(parentId, LdapPermission.CREATE);
		
		LdapEntry e = new LdapEntry();
		e.setType(type);
		e.setName(name);
		e.setDescription(description);
		
		if(type==EntryType.USER) {
			e.setEmail(email);
			e.setGivenname(givenname);
			e.setFamilyname(familyname);
			e.setDisplayname(displayname);
		}
		
		e = ldapRepository.create(e, parentId);
		e = saveInternal(e);
		
		String pass = TokenGenerator.generate(12);

		if(type==EntryType.USER) {
			ldapRepository.setPassword(e.getId(), pass);
			if(notify) {
				try {
					Map<String,Object> params = new HashMap<String, Object>();
					params.put("token", pass);
					params.put("user", update(e.getId()));
					params.put("actor", SessionHolder.get());
					mailService.sendMail(e.getEmail(), "main.newAccount", null, params);
					
				} catch (Exception e2) {
					log.warn("error sending signup mail",e2);
				}
			}
		}
		
		return e;
	}

	@Override
	public LdapEntry get(String id) throws LdapException, AuthException {
		if(id.equals("ROOT")) id = "00000000-0000-0000-0000-000000000000";
		checkPermission(id, LdapPermission.READ);
		return getWithPermission(id, LdapPermission.READ);
	}

	@Override
	public void setEnabled(String id, boolean enabled) throws LdapException, AuthException, NamingException, SqlException {
		checkPermission(id, LdapPermission.SET_USER_STATE);
		ldapRepository.enableUser(id, enabled);
		saveInternal(ldapRepository.getById(id));
	}

	@Override
	public List<LdapPermission> getPermissions(String id) throws LdapException, AuthException {
		List<LdapPermission> out = new ArrayList<LdapPermission>();
		AuthUtils.checkLoggedIn();
		if(AuthUtils.isAdmin()) {
			out.add(LdapPermission.ADMIN);
		}
		try {
			for(EntryAcl a : entryAclRepo.getEffectiveAcls(id, SessionHolder.get().getPrincipalIds())) {
				out.add(a.getPermission());
			}
			if(SessionHolder.get().getPrincipalIds().contains(id)) {
				out.add(LdapPermission.READ);
			}
		} catch (Exception e) {
			log.error("error gettting permissions",e);
			throw new LdapException("LDAP.SQL_FAILED", e.getMessage(), e);
		}
		return LdapPermission.fillUp(out);
	}
	
	public LdapEntry getByUsername(String username) throws LdapException, AuthException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<LdapEntry> list(EntryQuery eq) throws LdapException, AuthException {
		try {
			AuthUtils.checkLoggedIn();
			eq.setPrincipalIds(SessionHolder.get().getPrincipalIds());
			if(eq.getPermissions().size()==0) {
				if(AuthUtils.isAdmin()) {
					eq.setPrincipalIds(new ArrayList<String>());
				} else {
					return new ArrayList<>();
				}
			}
			if(!AuthUtils.isAdmin()) {
				eq.setIncludeIgnored(false);
			} 

			if(StringUtils.isEmpty(eq.getParentId())) {
				// no
			} else if (eq.getParentId().equals("ROOT")) {
				// no
			} else {
				LdapEntry e = entryRepo.get(eq.getParentId());
				if(e==null) {
					return new ArrayList<LdapEntry>();
				}
				if(e.isIgnored()) {
					return new ArrayList<LdapEntry>();
				}
			}
			
			return entryRepo.list(eq);
		} catch (SqlException e) {
			throw new LdapException("LDAP.LIST_FAILED", e.getMessage(), e);
		}
	}
	
	private LdapEntry saveInternal(LdapEntry e) throws InvalidNameException, LdapException, SqlException {
		return entryRepo.save(e);
	}

	public LdapEntry save(LdapEntry e) throws LdapException, AuthException, SqlException, InvalidNameException {
		checkPermission(e.getId(), LdapPermission.WRITE);
		e = ldapRepository.save(e);
		return saveInternal(e);
	}

	public void delete(LdapEntry e) throws LdapException, AuthException, SqlException {
		checkPermission(e.getId(), LdapPermission.DELETE);
		EntryQuery eq = new EntryQuery();
		eq = eq.setParentId(e.getId());
		List<LdapEntry> children = entryRepo.list(eq);
		if(children.size()>0) {
			throw new LdapException("LDAP_NOT_EMPTY", "this obect has children. please delete the child objects first", null);
		}
		ldapRepository.delete(e.getId());
		entryRepo.delete(e.getId());
		
	}

	@Override
	public void delete(String id) throws LdapException, AuthException, SqlException {
		delete(get(id));
	}
	
	@Override
	public void ignore(String id, boolean ignore) throws LdapException, AuthException {
		try {
			AuthUtils.checkAdmin();
			LdapEntry e = getWithPermission(id, LdapPermission.ADMIN);
			entryRepo.setIgnore(e.getPath(),ignore);
		} catch (Exception e) {
			throw new LdapException("LDAP.IGNORE_FAILED", "failed to set ignore state", e);
		}
	}

	public LdapEntry move(String entry, String toParent) throws Exception {
		AuthUtils.checkLoggedIn();
		LdapEntry e = getWithPermission(entry, LdapPermission.WRITE);
		LdapEntry p = getWithPermission(toParent, LdapPermission.CREATE);
		LdapEntry le = ldapRepository.move(e.getId(), p.getId());
		
		if(p.getPath().startsWith(e.getPath())) {
			throw new RuntimeException();
		}
		
		String r = this.root.getId();
		
		for(int i=0; i < Math.min(e.getAncestors().size(), p.getAncestors().size());i++) {
			if(e.getAncestors().get(i)[1].equals(p.getAncestors().get(i)[1])) {
				r = e.getAncestors().get(i)[1];
			} else {
				break;
			}
		}
		
		LdapEntry common = ldapRepository.getById(r);
		recursive(common);
		return le;
	}

	public void setPassword(String id, String oldPassword, String newPassword) throws LdapException, AuthException {
		AuthUtils.checkLoggedIn();
		if(id.equals(SessionHolder.get().getId())) {
			// self!
			log.info("ldap service setPassword: "+SessionHolder.get().getName());
			LdapEntry e = ldapRepository.authenticate(SessionHolder.get().getName(), oldPassword);
			log.info("ldap service setPassword: "+e.getName()+" / "+e.getDisplayname()+" ===> "+newPassword);
			ldapRepository.setPassword(e.getId(), newPassword);
		} else {
			checkPermission(id, LdapPermission.PW_RESET);
			if(StringUtils.isEmpty(newPassword)) {
				newPassword = new PasswordGenerator.PasswordGeneratorBuilder().useDigits(true).useLower(true).useUpper(true).build().generate(12);
			}
			ldapRepository.setPassword(id, newPassword);
		}
	}

	@Override
	public User update(String id) throws AuthException {
		
		if(id == null) {
			return new User();
		}
		try {
			LdapEntry e = entryRepo.get(id);
			User user = new User();
			
			user.setId(e.getId());
			user.setName(e.getName());
			user.setDisplayname(e.getDisplayname());
			user.setEmail(e.getEmail());
			SortedSet<String> principalIds = new TreeSet<String>();
			principalIds.add(e.getId());
			principalIds.addAll(Arrays.asList(e.getPath().split("\\.")));
			for(LdapEntry ep : ldapRepository.getMemberships(e.getId())) {
				principalIds.add(ep.getId());
				principalIds.addAll(Arrays.asList(ep.getPath().split("\\.")));
			}
			user.setNeedsReset(tokenRepo.hasToken(e.getId()));
			log.info("user principals: "+principalIds);
			user.setPrincipalIds(new ArrayList<String>(principalIds));
			user.setAdmin(entryAclRepo.isAdmin(principalIds));
			return user;
		} catch (Exception e2) {
			log.warn("something went wrong: ",e2);
			throw new AuthFailedException();
		}
	}

	@Override
	public User authenticate(String username, String password, List<String> groupIds) throws LdapException, AuthException {
		log.info("ldap service: authenticating: "+username);
		try {
			
			LdapEntry e = ldapRepository.authenticate(username, password);
			
			if(e == null) {
				log.info("ldap service: authenticating: user is null! ("+username+")");
				throw new NotAuthenticatedException();
			}
			
			try {
				entryAclRepo.checkFirst(e.getId());
			} catch (Exception e2) {
			}
			
			log.info("ldap service: authenticating: get user ... ");
			User user = update(e.getId());
			log.info("ldap service: authenticating: user is: "+user);
			if(groupIds == null || groupIds.size() == 0) {
				return user;
			}
			if(user.getPrincipalIds().containsAll(groupIds)) {
				return user;
			}
			
		} catch (Exception e) {
			log.warn("an error occured while authenticating", e);
		}
		throw new NotAuthorizedException();
	}

	public List<LdapEntry> getMembers(String id) throws LdapException, AuthException {
		try {
			checkPermission(id, LdapPermission.READ);
			LdapEntry e = getWithPermission(id, LdapPermission.READ);
			List<Membership> members = membershipRepo.list(e.getId(),null);
			if (members.size()==0) return new ArrayList<LdapEntry>();

			List<String> entryIds = new ArrayList<String>();
			for(Membership m : members) {
				entryIds.add(m.getPrincipalId());
			}
			log.info("finding members: "+members.size()+" found!");
			EntryQuery eq = new EntryQuery();
			eq.setEntryIds(entryIds);
			List<LdapEntry> mEntries = entryRepo.list(eq);
			log.info("finding members: "+mEntries.size()+" entries found!");
			return mEntries;
		} catch (Exception e) {
			e.printStackTrace();
			throw new LdapException("LDAP.LIST_MEMBERS_FAILED", e.getMessage(), e);
		}
	}

	public List<LdapEntry> getMemberships(String id) throws LdapException, AuthException {
		try {
			checkPermission(id, LdapPermission.PW_RESET);
			LdapEntry e = getWithPermission(id, LdapPermission.READ);
			List<Membership> members = membershipRepo.list(null,e.getId());
			if (members.size()==0) return new ArrayList<LdapEntry>();
			
			List<String> entryIds = new ArrayList<String>();
			for(Membership m : members) {
				entryIds.add(m.getGroupId());
			}
			EntryQuery eq = new EntryQuery();
			if(AuthUtils.isAdmin()) {
				// no limits
			} else {
				eq.addPermissions(LdapPermission.READ);
				eq.setPrincipalIds(SessionHolder.get().getPrincipalIds());
			}
			eq.setEntryIds(entryIds);
			return entryRepo.list(eq);
		} catch (Exception e) {
			throw new LdapException("LDAP.LIST_MEMBERS_FAILED", e.getMessage(), e);
		}
	}

	public void addMember(String groupId, String principalId) throws LdapException, AuthException, SqlException {
		checkPermission(principalId, LdapPermission.READ);
		checkPermission(groupId, LdapPermission.EDIT_MEMBERS);
		ldapRepository.addMember(groupId, principalId);
		Membership m = new Membership();
		m.setGroupId(groupId);
		m.setPrincipalId(principalId);
		membershipRepo.save(m);
	}

	public void removeMember(String groupId, String principalId) throws LdapException, AuthException, SqlException {
		checkPermission(groupId, LdapPermission.EDIT_MEMBERS);
		ldapRepository.removeMember(groupId, principalId);
		Membership m = membershipRepo.get(groupId, principalId);
		if(m!=null) {
			membershipRepo.delete(m);
		}
	}

	@Override
	public List<EntryAcl> listAcls(String id) throws SqlException, AuthException {
		checkPermission(id, LdapPermission.ACL_READ);
		return entryAclRepo.listAcls(id);
	}

	@Override
	public EntryAcl addAcl(EntryAcl acl) throws SqlException, AuthException, LdapException {
		LdapEntry e = getWithPermission(acl.getEntryId(), LdapPermission.ACL_WRITE);
		acl.setPath(e.getPath()+(acl.isRecursive()?"%":""));
		return entryAclRepo.save(acl);
	}

	@Override
	public void deleteAcl(String entryId, String aclId) throws SqlException, AuthException {
		checkPermission(entryId, LdapPermission.ACL_WRITE);
		entryAclRepo.deleteAcl(entryId, aclId);
	}
	
	
	@Override
	public void reset(String username) {
		try {
			LdapEntry le = ldapRepository.getByUsername(username);
			if(le!=null) {
				ResetToken token = tokenRepo.create(le.getId());
				User u = update(le.getId());
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("token", token.getToken());
				params.put("user", u);
				if(mailService.sendMail(le.getEmail(), "main.pwReset", null, params)) {
					return;
				}
			} else {
				return;
			}
		} catch (Exception e) {
		}
		throw new RuntimeException("reset failed");
	}

	@Override
	public void updatePassword(String newPassword) throws NotAuthorizedException, SqlException, LdapException {
		User u = SessionHolder.get();
		if(u==null) {
		} else if (tokenRepo.hasToken(u.getId())) {
			ldapRepository.setPassword(u.getId(), newPassword);
			tokenRepo.delete(u.getId());
			return;
		}
		throw new NotAuthorizedException();
		
	}
	
}
