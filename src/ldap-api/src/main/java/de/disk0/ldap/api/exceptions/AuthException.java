package de.disk0.ldap.api.exceptions;

public class AuthException extends Exception {

	private static final long serialVersionUID = 2450028013632305608L;

	public AuthException(String code, String message, Throwable cause) {
		super(message,cause);
	}
	
}
