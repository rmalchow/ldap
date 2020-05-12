package de.disk0.ldap.api.entities.query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.disk0.ldap.api.entities.EntryType;
import de.disk0.ldap.api.entities.LdapPermission;

public class EntryQuery {

	private String entryId;
	private List<String> entryIds;
	private String parentId;
	private String username;
	private List<EntryType> types;
	private List<String> principalIds;
	private List<LdapPermission> permissions;
	private boolean includeIgnored;
	private String order;
	private String filter;
	private int offset = 0;
	private int max = 10000;

	public String getEntryId() {
		return entryId;
	}

	public EntryQuery setEntryId(String entryId) {
		this.entryId = entryId;
		return this;
	}

	public List<String> getEntryIds() {
		return entryIds;
	}

	public EntryQuery setEntryIds(List<String> entryIds) {
		this.entryIds = entryIds;
		return this;
	}

	public String getParentId() {
		return parentId;
	}

	public EntryQuery setParentId(String parentId) {
		this.parentId = parentId;
		return this;
	}

	public List<EntryType> getTypes() {
		return types;
	}

	public EntryQuery setTypes(List<EntryType> types) {
		this.types = types;
		return this;
	}

	public List<String> getPrincipalIds() {
		return principalIds;
	}

	public EntryQuery setPrincipalIds(List<String> principalIds) {
		this.principalIds = principalIds;
		return this;
	}

	public List<LdapPermission> getPermissions() {
		if(permissions==null) permissions = new ArrayList<>();
		return permissions;
	}

	public void addPermissions(LdapPermission... permissions) {
		if(this.permissions==null) this.permissions = new ArrayList<>();
		this.permissions.addAll(Arrays.asList(permissions));
	}

	public EntryQuery setPermissions(List<LdapPermission> permissions) {
		this.permissions = permissions;
		return this;
	}

	public String getFilter() {
		return filter;
	}

	public EntryQuery setFilter(String filter) {
		this.filter = filter;
		return this;
	}

	public int getOffset() {
		return offset;
	}

	public EntryQuery setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	public int getMax() {
		return max;
	}

	public EntryQuery setMax(int max) {
		this.max = max;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public EntryQuery setUsername(String username) {
		this.username = username;
		return this;
	}

	
	public static EntryQuery create() {
		return new EntryQuery();
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public boolean isIncludeIgnored() {
		return includeIgnored;
	}

	public void setIncludeIgnored(boolean includeIgnored) {
		this.includeIgnored = includeIgnored;
	}
	
}
