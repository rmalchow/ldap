package de.disk0.ldap.api.totp;

public interface TotpCallback {
	
	public String callback(String dn, String password);

}
