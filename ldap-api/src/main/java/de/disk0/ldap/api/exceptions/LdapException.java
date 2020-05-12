package de.disk0.ldap.api.exceptions;

public class LdapException extends Exception {

	public LdapException(String code, String message, Throwable cause) {
		super(message,cause);
	}
	
}
