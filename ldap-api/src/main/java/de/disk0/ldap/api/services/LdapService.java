package de.disk0.ldap.api.services;

import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;

import de.disk0.dbutil.api.exceptions.SqlException;
import de.disk0.ldap.api.entities.Complaint;
import de.disk0.ldap.api.entities.EntryAcl;
import de.disk0.ldap.api.entities.EntryType;
import de.disk0.ldap.api.entities.LdapEntry;
import de.disk0.ldap.api.entities.LdapPermission;
import de.disk0.ldap.api.entities.query.EntryQuery;
import de.disk0.ldap.api.entities.session.User;
import de.disk0.ldap.api.exceptions.AuthException;
import de.disk0.ldap.api.exceptions.LdapException;
import de.disk0.ldap.api.exceptions.NotAuthorizedException;

public interface LdapService {
	
	public LdapEntry get(String id) throws LdapException, AuthException;
	public LdapEntry getByUsername(String username) throws LdapException, AuthException;
	public List<LdapEntry> list(EntryQuery eq) throws LdapException, AuthException;

	public LdapEntry save(LdapEntry e) throws LdapException, AuthException, SqlException, InvalidNameException;
	public void delete(LdapEntry e) throws LdapException, AuthException;

	public LdapEntry move(String entry, String toParent) throws Exception;

	public void setPassword(String id, String oldPassword, String newPassword) throws LdapException, AuthException;
	
	public void setEnabled(String id, boolean enabled) throws LdapException, AuthException, NamingException, SqlException;
	
	public User authenticate(String username, String password, List<String> groupIds) throws LdapException, AuthException; 
	public User update(String id) throws AuthException;
	
	public List<LdapEntry> getMembers(String id) throws LdapException, AuthException;
	public List<LdapEntry> getMemberships(String id) throws LdapException, AuthException;
	
	public void addMember(String groupId, String principalId) throws LdapException, AuthException, SqlException;
	public void removeMember(String groupId, String principalId) throws LdapException, AuthException, SqlException;

	public List<LdapPermission> getPermissions(String id) throws LdapException, AuthException;

	public void ignore(String id, boolean ignore) throws LdapException, AuthException;

	public List<Complaint> checkCreate(
			String parentId, EntryType type, 
			String name, 
			String givenname, 
			String familyname, 
			String displayname, 
			String email, 
			String description, 
			boolean notify) throws SqlException, LdapException;

	public LdapEntry create(
			String parentId, EntryType type, 
			String name, 
			String givenname,  
			String familyname,  
			String displayname, 
			String email, 
			String description, 
			boolean notify) throws NotAuthorizedException, LdapException, AuthException, InvalidNameException, SqlException;

	public List<EntryAcl> listAcls(String id) throws SqlException, AuthException, LdapException;

	public EntryAcl addAcl(EntryAcl acl) throws SqlException, AuthException, LdapException;

	public void deleteAcl(String entryId, String aclId) throws SqlException, AuthException;
	
	public void reset(String username);
	public void updatePassword(String newPassword) throws NotAuthorizedException, SqlException, LdapException;

	
}
