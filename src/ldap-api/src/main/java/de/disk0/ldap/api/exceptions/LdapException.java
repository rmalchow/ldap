package de.disk0.ldap.api.exceptions;

public class LdapException extends Exception {

	private static final long serialVersionUID = 8530525005654771308L;

	public LdapException(String code, String message, Throwable cause) {
		super(message,cause);
	}
	
}
