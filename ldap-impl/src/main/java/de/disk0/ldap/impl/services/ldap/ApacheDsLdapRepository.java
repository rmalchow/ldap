package de.disk0.ldap.impl.services.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.api.ldap.model.cursor.Cursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.AddRequest;
import org.apache.directory.api.ldap.model.message.AddRequestImpl;
import org.apache.directory.api.ldap.model.message.AliasDerefMode;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.server.core.api.CoreSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.stereotype.Component;

import de.disk0.ldap.apache.ApacheDsEmbedded;
import de.disk0.ldap.api.entities.EntryType;
import de.disk0.ldap.api.entities.LdapEntry;
import de.disk0.ldap.api.exceptions.AuthException;
import de.disk0.ldap.api.exceptions.LdapException;
import de.disk0.ldap.api.services.LdapRepository;

@Component
public class ApacheDsLdapRepository implements LdapRepository {
	
	private static Log log = LogFactory.getLog(ApacheDsLdapRepository.class);

	@Autowired
	private ApacheDsEmbedded embeddedADS;
	
	public static final String rootId = "00000000-0000-0000-0000-000000000000"; 

	private static String[] atts = new String[] { 
			"sambaAcctFlags",
			"businessCategory", "cn",
			"dc", "description", 
			"displayname", 
			"distinguishedName", 
			"givenname", 
			"l",
			"mail", "member", "memberOf", "mobile", "name", "entryUuid", "entryParentId", "o",
			"objectClass", "ou", "postalCode", "sn", "userPrincipalName", "streetAddress", "surName",
			"telephoneNumber", "uid", "userPassword" 
		};

	private String getAttribute(Entry e, String... attributeNames) throws LdapInvalidAttributeValueException {
		for (String s : attributeNames) {
			if (e.containsAttribute(s)) {
				return e.get(s).getString();
			}
		}
		return null;
	}
	
	private String getPath(String id) throws LdapException {
		try {
			String out = id;
			SearchRequest sr = new SearchRequestImpl();
			sr.setBase(new Dn());
			sr.setScope(SearchScope.SUBTREE);
			sr.addAttributes("entryParentId", "entryUuid");
			sr.setFilter(new EqualsFilter("entryUuid", id).encode());
			
			Cursor<Entry> cursor = embeddedADS.getAdminSession().search(sr);
			if(cursor.next()) {
				Entry e = cursor.get();
				out = getPath(getAttribute(e, "entryParentId"))+"."+id;
			}
			return out;

		} catch (Exception e) {
			throw new LdapException("GET_ANCESTORS_FAILED", "failed to retrieve ancestors", e);
		}
	}
	
	
	private List<LdapEntry> mapFromLdap(Cursor<Entry> cursor) throws LdapInvalidAttributeValueException, LdapException {
		try {
			List<LdapEntry> out = new ArrayList<LdapEntry>();
			while(cursor.next()) {
				Entry e = cursor.get();
				if(!e.getDn().toString().equals("ou=schema")) out.add(mapFromLdap(e));
			}
			log.info("ldap repo: find: found: "+out.size());
			return out;
		} catch (Exception e) {
			throw new LdapException("MAPPING_FAILED", "failed to map entries", e);
		}
	}
	
	private LdapEntry mapFromLdap(Entry e) throws LdapInvalidAttributeValueException, LdapException {

		LdapEntry le = new LdapEntry();
		le.setId(getAttribute(e, "entryUuid"));
		le.setParentId(getAttribute(e, "entryParentId"));
		
		le.setDn(e.getDn().getName());
		le.setDisplayname(getAttribute(e, "displayname", "cn", "userPrincipalName",  "uid", "ou", "o", "dc"));
		le.setDescription(getAttribute(e, "description"));
		le.setName(getAttribute(e, "userPrincipalName","uid", "cn", "mail", "ou", "dc"));
		le.setEmail(getAttribute(e, "mail"));
		le.setFamilyname(getAttribute(e, "sn"));
		le.setGivenname(getAttribute(e, "givenname"));
		
		le.setPath(getPath(le.getId()));
		
		if(e.getDn().isRootDse()) {
			le.setDisplayname("/");
			le.setId(rootId);
		}
		
		if(e.contains("objectClass","groupOfNames")) {
			le.setType(EntryType.GROUP);
		} else if(e.contains("objectClass","groupOfUniqueNames")) {
			le.setType(EntryType.GROUP);
		} else if(e.contains("objectClass","person")) {
			le.setType(EntryType.USER);
		} else if(e.contains("objectClass","inetOrgPerson")) {
			le.setType(EntryType.USER);
		} else {
			le.setType(EntryType.UNIT);
		}
		
		return le;
	}
	

	@Override
	public LdapEntry getRoot() throws LdapException {
		try {
			Entry e = embeddedADS.getAdminSession().lookup(new Dn(), atts);
			LdapEntry le = mapFromLdap(e);
			return le;
		} catch (Exception e) {
			throw new LdapException("GET_ROOT_FAILED", "Failed to get ROOT DSE", e);
		}
	}

	private Entry getEntryById(String id, String... as) throws LdapException {
		try {
			if(id.equals(rootId)) {
				return embeddedADS.getAdminSession().lookup(new Dn(), as.length==0?atts:as);
			}
			SearchRequest sr = new SearchRequestImpl();
			sr.setBase(new Dn());
			sr.setScope(SearchScope.SUBTREE);
			sr.addAttributes(atts);
			sr.setFilter(new EqualsFilter("entryUuid", id).encode());
			Cursor<Entry> cursor = embeddedADS.getAdminSession().search(sr);
			if(cursor.next()) {
				return cursor.get();
			}
			return null;
		} catch (Exception e) {
			throw new LdapException("GET_BY_ID_FAILED", "Failed to get entry by ID",e);
		}
	}

	@Override
	public LdapEntry getById(String id) throws LdapException {
		return getLdapEntryById(id,atts);
	}
	
	private LdapEntry getLdapEntryById(String id, String... as) throws LdapException {
		try {
			return mapFromLdap(getEntryById(id, as));
		} catch (Exception e) {
			throw new LdapException("GET_BY_ID_FAILED", "Failed to get entry by ID", e);
		}
	}

	@Override
	public LdapEntry getByUsername(String username) throws LdapException {
		try {
			SearchRequest sr = new SearchRequestImpl();
			sr.setBase(new Dn());
			sr.setScope(SearchScope.SUBTREE);
			sr.addAttributes(atts);
			
			OrFilter of = new OrFilter();
			of.append(new EqualsFilter("userPrincipalName", username));
			of.append(new EqualsFilter("uid", username));
			sr.setFilter(of.encode());

			Cursor<Entry> cursor = embeddedADS.getAdminSession().search(sr);
			if(cursor.next()) {
				Entry e = cursor.get();
				return mapFromLdap(e);
			}
			return null;
		} catch (Exception e) {
			throw new LdapException("GET_BY_ID_FAILED", "Failed to get entry by ID", e);
		}
	}
	
	@Override
	public List<LdapEntry> getChildren(LdapEntry parent) throws LdapException {
		try {
			List<LdapEntry> out = mapFromLdap(embeddedADS.getAdminSession().list(new Dn(parent.getDn()), AliasDerefMode.NEVER_DEREF_ALIASES, atts));
			log.info("ldap repo: get children: "+out.size());
			return out;
		} catch (Exception e) {
			throw new LdapException("SEARCH_FAILED", "Failed to perform a search", e);
		}
	}

	@Override
	public List<LdapEntry> getAncestors(String id) throws LdapException {
		try {
			List<LdapEntry> out = new ArrayList<>();
			LdapEntry e = getById(id);

			if(e.getParentId() != null) {
				out.addAll(getAncestors(e.getParentId()));
			}
			out.add(e);
			return out;

		} catch (Exception e) {
			throw new LdapException("GET_ANCESTORS_FAILED", "failed to retrieve ancestors", e);
		}

	}

	@Override
	public LdapEntry create(LdapEntry e, String parentId) throws LdapException {

		CoreSession cs = embeddedADS.getAdminSession();
		try {
			Entry ee = new DefaultEntry();
			
			Entry parent = getEntryById(parentId, atts);
			
			if(e.getDescription()!=null && e.getDescription().length()>0) ee.add(new DefaultAttribute("description",e.getDescription()));
			
			AddRequest ar = new AddRequestImpl();

			if(e.getType() == EntryType.GROUP) {
				log.info("apache repo: create: group: "+ee.getDn().toString());
				ee.add(new DefaultAttribute("objectClass","groupOfNames"));
				ee.add(new DefaultAttribute("cn",e.getName()));
				ar.setEntry(ee);
				ar.setEntryDn(new Dn(new Rdn("cn",e.getName()),parent.getDn()));
				cs.add(ar);
			} if(e.getType() == EntryType.UNIT) {
				log.info("apache repo: create: unit: "+ee.getDn().toString());
				ee.add(new DefaultAttribute("objectClass","organizationalUnit"));
				ee.add(new DefaultAttribute("ou",e.getName()));
				ar.setEntry(ee);
				ar.setEntryDn(new Dn(new Rdn("ou",e.getName()),parent.getDn()));
				cs.add(ar);
			} if(e.getType() == EntryType.USER) {
				log.info("apache repo: create: user: "+ee.getDn().toString());
				ee.add(new DefaultAttribute("objectClass","inetOrgPerson"));
				ee.add(new DefaultAttribute("objectClass","simulatedMicrosoftSecurityPrincipal"));
				if(e.getEmail()!=null) ee.add(new DefaultAttribute("mail",e.getEmail()));
				ee.add(new DefaultAttribute("cn",e.getDisplayname()));
				ee.add(new DefaultAttribute("displayname",e.getDisplayname()));
				ee.add(new DefaultAttribute("userPrincipalName",e.getName()));
				ee.add(new DefaultAttribute("sn",e.getFamilyname()));
				ee.add(new DefaultAttribute("givenname",e.getGivenname()));
				ee.add(new DefaultAttribute("sAMAccountName",e.getName()));
				ar.setEntry(ee);
				ar.setEntryDn(new Dn(new Rdn("uid",e.getName()),parent.getDn()));
				cs.add(ar);
			}
			 
			
			return mapFromLdap(cs.lookup(ee.getDn(), atts));
				
		} catch (Exception ex) {
			throw new LdapException("SAVE_ENTRY_FAILED", "failed to save entry", ex);
		}
	}

	private List<Modification> setAttribute(Entry e, String att, String value) {
		List<Modification> out = new ArrayList<Modification>();
		try {
			Attribute ex = e.get(att);
			ModificationOperation op = null;
			if(StringUtils.isEmpty(value)) value = null;
			if(value!=null) {
				if(ex==null) {
					out.add(new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, new DefaultAttribute(att,value)));
				} else {
					out.add(new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, new DefaultAttribute(att,value)));
				}
			} else {
				if(ex!=null) {
					out.add(new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE, new DefaultAttribute(att)));
				}
			}
		} catch (Exception e2) {
		}
		return out;
	}
	
	@Override
	public LdapEntry save(LdapEntry e) throws LdapException {

		try {
			CoreSession cs = embeddedADS.getAdminSession();
			Entry ee = getEntryById(e.getId(), atts);
			List<Modification> out = new ArrayList<Modification>();
			out.addAll(setAttribute(ee, "description", e.getDescription()));
			if(e.getType()==EntryType.USER) {
				out.addAll(setAttribute(ee, "mail", e.getEmail()));
				out.addAll(setAttribute(ee, "displayname", e.getDisplayname()));
				out.addAll(setAttribute(ee, "sn", e.getFamilyname()));
				out.addAll(setAttribute(ee, "givenname", e.getGivenname()));
			}

			for(Modification m : out) {
				log.info(" ----> "+m.getOperation()+" == "+m.getAttribute().toString());
			}
			
			cs.modify(ee.getDn(), out);
			return getById(e.getId());
		} catch (Exception ex) {
			throw new LdapException("SAVE_ENTRY_FAILED", "failed to save entry", ex);
		}
			
	}

	@Override
	public void delete(String id) throws LdapException {
	}

	@Override
	public LdapEntry move(String id, String newParent) throws LdapException {
		return null;
	}

	@Override
	public void setPassword(String id, String password) throws LdapException {	
		try {
			LdapEntry le = getById(id);
			Entry entry = embeddedADS.getAdminSession().lookup(new Dn(le.getDn()),atts);
			Attribute a = new DefaultAttribute("userPassword",  password);
			Modification m = null;
			if(entry.containsAttribute("userPassword")) {
				m = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, a);
			} else {
				m = new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, a);
			}
			embeddedADS.getAdminSession().modify(entry.getDn(), Collections.singletonList(m));
		} catch (Exception e) {
			throw new LdapException("AUTHENTICATE_FAILED", "Failed to authenticate", e);
		}
	}

	@Override
	public LdapEntry authenticate(String username, String password) throws LdapException, AuthException {
		try {
			LdapEntry le = getByUsername(username); 
			Entry e = embeddedADS.getAdminSession().lookup(new Dn(le.getDn()),atts);
			CoreSession s = embeddedADS.getAdminSession().getDirectoryService().getSession(e.getDn(),password.getBytes("utf-8"));
			LdapEntry lea = mapFromLdap(s.lookup(new Dn(le.getDn()),atts));
			return lea;
		} catch (Exception e) {
			throw new LdapException("AUTHENTICATE_FAILED", "Failed to authenticate", e);
		}
	}

	@Override
	public List<LdapEntry> getMembers(String id) throws LdapException {
		try {
			List<LdapEntry> out = new ArrayList<>();
			
			CoreSession cs = embeddedADS.getAdminSession(); 
			
			Entry g = getEntryById(id, "member");
			
			if(!g.containsAttribute("member")) return out;
			
			Iterator<Value<?>> vi = g.get("member").iterator();
			while(vi.hasNext()) {
				Value v = vi.next();
				out.add(mapFromLdap(cs.lookup(new Dn(v.toString()), atts)));
			}
			return out;
		} catch (Exception e) {
			throw new LdapException("AUTHENTICATE_FAILED", "Failed to authenticate", e);
		}
	}

	@Override
	public List<LdapEntry> getMemberships(String id) throws LdapException {
		try {
			List<LdapEntry> out = new ArrayList<>();
			
			CoreSession cs = embeddedADS.getAdminSession(); 
			
			Entry g = getEntryById(id, "memberOf"); 
			
			if(!g.containsAttribute("memberOf")) return out;
			
			Iterator<Value<?>> vi = g.get("memberOf").iterator();
			while(vi.hasNext()) {
				Value v = vi.next();
				out.add(mapFromLdap(cs.lookup(new Dn(v.toString()), atts)));
			}
			return out;
		} catch (Exception e) {
			throw new LdapException("AUTHENTICATE_FAILED", "Failed to authenticate", e);
		}
	}

	@Override
	public void addMember(String groupId, String principalId) throws LdapException {
		try {

			Entry group = getEntryById(groupId, "member");
			Entry user = getEntryById(principalId, "memberOf");

			CoreSession cs = embeddedADS.getAdminSession();
			
			log.info("ldap repo: add member: add: "+user.getDn()+" to: "+group.getDn());

			if(!group.containsAttribute("member")) {
				log.info("ldap repo: addMember: add member attribute and user dn!");
				Attribute a = new DefaultAttribute("member");
				a.add(user.getDn().toString());
				Modification m = new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, a);
				cs.modify(group.getDn(), Collections.singletonList(m));
			} else if (!group.contains("member", user.getDn().toString())) {
				log.info("ldap repo: addMember: add member user dn!");
				Attribute a = group.get("member");
				a.add(user.getDn().toString());
				Modification m = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, a);
				cs.modify(group.getDn(), Collections.singletonList(m));
			}

			/**
			if(!user.containsAttribute("memberOf")) {
				log.info("ldap repo: addMember: add memberOf attribute and group dn!");
				Attribute a = new DefaultAttribute("memberOf");
				a.add(group.getDn().toString());
				Modification m = new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, a);
				cs.modify(user.getDn(), Collections.singletonList(m));
			} else if (!group.contains("memberOf", group.getDn().toString())) {
				log.info("ldap repo: addMember: add memberOf group dn!");
				Attribute a = group.get("memberOf");
				a.add(group.getDn().toString());
				Modification m = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, a);
				cs.modify(user.getDn(), Collections.singletonList(m));
			}
			**/

		} catch (Exception e) {
			throw new LdapException("AUTHENTICATE_FAILED", "Failed to authenticate", e);
		}
		
	}

	@Override
	public void removeMember(String groupId, String principalId) throws LdapException {
		try {

			Entry group = getEntryById(groupId, "member");
			Entry user = getEntryById(principalId, "memberOf");
			
			CoreSession cs = embeddedADS.getAdminSession();

			Attribute a = group.get("member");
			if(a!=null || !a.contains(user.getDn().toString())) {
				a.remove(user.getDn().toString());
				if(a.size()==0) {
					log.info("ldap repo: removeMember: remove member attribute!");
					Modification m = new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE, a);
					cs.modify(group.getDn(), Collections.singletonList(m));
				} else {
					log.info("ldap repo: removeMember: remove member attribute user dn value!");
					Modification m = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, a);
					cs.modify(group.getDn(), Collections.singletonList(m));
				}
			}
			
		} catch (Exception e) {
			throw new LdapException("AUTHENTICATE_FAILED", "Failed to authenticate", e);
		}
	}

	@Override
	public void enableUser(String id, boolean enable) throws LdapException {
	}

}
