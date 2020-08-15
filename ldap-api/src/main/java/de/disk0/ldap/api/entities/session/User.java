package de.disk0.ldap.api.entities.session;

import java.util.ArrayList;
import java.util.List;

import de.disk0.ldap.api.utils.TokenGenerator;

public class User {

	private String id;
	private String name;
	private String displayname;
	private String email;
	private boolean admin = false;
	private boolean needsReset = true;
	private List<String> principalIds = new ArrayList<String>();
	private String xsrf = TokenGenerator.generate(64);

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public List<String> getPrincipalIds() {
		return principalIds;
	}

	public void setPrincipalIds(List<String> principalIds) {
		this.principalIds = principalIds;
	}

	public String getXsrf() {
		return xsrf;
	}

	public void setXsrf(String xsrf) {
		this.xsrf = xsrf;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "{ displayname : "+displayname+", email: "+email+", id: "+id+" }";
	}
	
	public boolean isLoggedIn() {
		return id != null;
	}
	
	public void setLoggedIn(boolean in) {
		// do nothing
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNeedsReset() {
		return needsReset;
	}

	public void setNeedsReset(boolean needsReset) {
		this.needsReset = needsReset;
	}
	
	
}
