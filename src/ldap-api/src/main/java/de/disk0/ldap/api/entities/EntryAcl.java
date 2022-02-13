package de.disk0.ldap.api.entities;

import javax.persistence.Column;
import javax.persistence.Table;

import de.disk0.dbutil.api.entities.BaseGuidEntity;

@Table(name="entry_acl")
public class EntryAcl extends BaseGuidEntity {

	@Column(name="entry_id")
	private String entryId;

	private LdapEntry entry;
	
	@Column(name="path")
	private String path;
	
	@Column(name="principal_id")
	private String principalId;

	private LdapEntry principal;

	@Column(name="permission")
	private LdapPermission permission;

	@Column(name="recursive")
	private boolean recursive;
	
	public String getEntryId() {
		return entryId;
	}

	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	public String getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	public LdapPermission getPermission() {
		return permission;
	}

	public void setPermission(LdapPermission permission) {
		this.permission = permission;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String entryPath) {
		this.path = entryPath;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public LdapEntry getPrincipal() {
		return principal;
	}

	public void setPrincipal(LdapEntry principal) {
		this.principal = principal;
	}

	public LdapEntry getEntry() {
		return entry;
	}

	public void setEntry(LdapEntry entry) {
		this.entry = entry;
	}
	
}
