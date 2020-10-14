package de.disk0.ldap.impl.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.disk0.dbutil.api.exceptions.SqlException;
import de.disk0.ldap.api.entities.Complaint;
import de.disk0.ldap.api.entities.LdapEntry;
import de.disk0.ldap.api.entities.EntryAcl;
import de.disk0.ldap.api.entities.EntryType;
import de.disk0.ldap.api.entities.LdapPermission;
import de.disk0.ldap.api.entities.query.EntryQuery;
import de.disk0.ldap.api.exceptions.AuthException;
import de.disk0.ldap.api.exceptions.LdapException;
import de.disk0.ldap.api.services.LdapService;

@RestController
@RequestMapping(value = "/api/ldap/entries")
public class LdapController {
	
	@Autowired
	private LdapService ldapService;
	
	@GetMapping
	public List<LdapEntry> list(
			@RequestParam(required = false) String parentId,
			@RequestParam(required = false) List<EntryType> type,
			@RequestParam(required = false) List<LdapPermission> permission,
			@RequestParam(required = false, defaultValue = "false") boolean includeIgnored,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) String order,
			@RequestParam(required = false, defaultValue = "true") boolean ascending,
			@RequestParam(required = false, defaultValue = "0") int offset,
			@RequestParam(required = false, defaultValue = "25") int max
		
			) throws Exception {
		
		EntryQuery eq = new EntryQuery();
		eq.setParentId(parentId);
		eq.setTypes(type);
		eq.setPermissions(permission);
		eq.setFilter(filter);
		eq.setOrder(order);
		eq.setOffset(offset);
		eq.setMax(max);
		eq.setIncludeIgnored(includeIgnored);
		
		return ldapService.list(eq);
	}
	
	
	@GetMapping(value = "/{id}")
	public LdapEntry get(@PathVariable String id)  throws Exception {
		return ldapService.get(id);
	}
	
	@PutMapping(value = "/{id}")
	public LdapEntry save(@PathVariable String id, @RequestBody LdapEntry entry)  throws Exception {
		entry.setId(id);
		return ldapService.save(entry);
	}
	
	@ResponseStatus(value = org.springframework.http.HttpStatus.NO_CONTENT)
	@PutMapping(value = "/{id}/password")
	public void setPassword(@PathVariable String id, @RequestParam(required = false) String oldPassword, @RequestParam(required = false) String newPassword)  throws Exception {
		ldapService.setPassword(id, oldPassword, newPassword);
	}
	
	@ResponseStatus(value = org.springframework.http.HttpStatus.NO_CONTENT)
	@PutMapping(value = "/{id}/status")
	public void setStatus(@PathVariable String id, @RequestParam boolean enabled)  throws Exception {
		ldapService.setEnabled(id, enabled);
	}
	
	@ResponseStatus(value = org.springframework.http.HttpStatus.NO_CONTENT)
	@PostMapping(value = "/{id}/move")
	public void move(@PathVariable String id, @RequestParam String newParentId)  throws Exception {
		ldapService.move(id,newParentId);
	}
	
	@PutMapping(value = "/{id}/create")
	public List<Complaint> checkCreate(
			@PathVariable String id,
			@RequestParam(required = false) EntryType type,
			@RequestParam(required = false, defaultValue = "") String name,
			@RequestParam(required = false, defaultValue = "") String givenname,
			@RequestParam(required = false, defaultValue = "") String familyname,
			@RequestParam(required = false, defaultValue = "") String displayname,
			@RequestParam(required = false, defaultValue = "") String email,
			@RequestParam(required = false, defaultValue = "") String description,
			@RequestParam(required = false, defaultValue = "false") boolean setPassword,
			@RequestParam(required = false, defaultValue = "") String pass1,
			@RequestParam(required = false, defaultValue = "") String pass2
			)  throws Exception {
		return ldapService.checkCreate(id,type,name,givenname,familyname,displayname,email,description,setPassword,pass1,pass2);
	}

	@PostMapping(value = "/{id}/create")
	public LdapEntry create(
			@PathVariable String id,
			@RequestParam(required = false) EntryType type,
			@RequestParam(required = false, defaultValue = "") String name,
			@RequestParam(required = false, defaultValue = "") String givenname,
			@RequestParam(required = false, defaultValue = "") String familyname,
			@RequestParam(required = false, defaultValue = "") String displayname,
			@RequestParam(required = false, defaultValue = "") String email,
			@RequestParam(required = false, defaultValue = "") String description,
			@RequestParam(required = false, defaultValue = "false") boolean setPassword,
			@RequestParam(required = false, defaultValue = "") String pass1,
			@RequestParam(required = false, defaultValue = "") String pass2
			)  throws Exception {
		return ldapService.create(id,type,name,givenname, familyname, displayname,email,description,setPassword,pass1,pass2);
	}
	
	@ResponseStatus(value = org.springframework.http.HttpStatus.NO_CONTENT)
	@PostMapping(value = "/{id}/ignore")
	public void ignore(@PathVariable String id, @RequestParam boolean ignore)  throws Exception {
		ldapService.ignore(id, ignore);
	}
	
	@GetMapping(value = "/{id}/members")
	public List<LdapEntry> getMembers(@PathVariable String id)  throws Exception {
		return ldapService.getMembers(id);
	}
	
	@GetMapping(value = "/{id}/memberships")
	public List<LdapEntry> getMemberShips(@PathVariable String id)  throws Exception {
		return ldapService.getMemberships(id);
	}
	
	@ResponseStatus(value = org.springframework.http.HttpStatus.NO_CONTENT)
	@PostMapping(value = "/{id}/members")
	public void addMembers(@PathVariable String id, @RequestParam String principalId)  throws Exception {
		ldapService.addMember(id,principalId);
	}
	
	@ResponseStatus(value = org.springframework.http.HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{id}/members/{principalId}")
	public void removeMembers(@PathVariable String id, @PathVariable String principalId)  throws Exception {
		ldapService.removeMember(id,principalId);
	}
	
	@GetMapping(value = "/{id}/acls")
	public List<EntryAcl> listAcls(@PathVariable String id) throws SqlException, AuthException, LdapException {
		return ldapService.listAcls(id);
	}
	
	@PostMapping(value = "/{id}/acls")
	public EntryAcl createAcl(@PathVariable String id, @RequestBody EntryAcl acl) throws SqlException, AuthException, LdapException {
		acl.setEntryId(id);
		return ldapService.addAcl(acl);
	}
	
	@ResponseStatus(value = org.springframework.http.HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{id}/acls/{aclId}")
	public void deleteAcl(@PathVariable String id,@PathVariable String aclId) throws SqlException, AuthException {
		ldapService.deleteAcl(id, aclId);
	}
	
	
	@GetMapping(value = "/{id}/permissions")
	public List<LdapPermission> getPermissions(@PathVariable String id)  throws Exception {
		return ldapService.getPermissions(id);
	}
	
	
	

}
