package de.disk0.ldap.api.services;



import java.util.List;

import de.disk0.ldap.api.entities.LdapEntry;
import de.disk0.ldap.api.exceptions.AuthException;
import de.disk0.ldap.api.exceptions.LdapException;

public interface LdapRepository {
	
	
	public LdapEntry create(LdapEntry e, String parentId) throws LdapException;

	public LdapEntry getById(String id) throws LdapException;
	public LdapEntry getByUsername(String username) throws LdapException;

	public List<LdapEntry> getChildren(LdapEntry parent) throws LdapException;
	public List<LdapEntry> getAncestors(String id) throws LdapException;

	public LdapEntry save(LdapEntry e) throws LdapException;
	public void delete(String id) throws LdapException;
	public LdapEntry move(String id, String newParent) throws LdapException, Exception;

	public void setPassword(String id, String password) throws LdapException;
	
	public LdapEntry authenticate(String username, String password) throws LdapException, AuthException; 
	
	public List<LdapEntry> getMembers(String id) throws LdapException;
	public List<LdapEntry> getMemberships(String id) throws LdapException;
	
	public void addMember(String groupId, String principalId) throws LdapException;
	public void removeMember(String groupId, String principalId) throws LdapException;

	public LdapEntry getRoot() throws LdapException;

	public void enableUser(String id, boolean enable) throws LdapException;




	
	
}
