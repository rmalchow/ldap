package de.disk0.ldap.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code =  HttpStatus.UNAUTHORIZED)
public class NotAuthenticatedException extends AuthException {

	private static final long serialVersionUID = -5430787691225780740L;

	public NotAuthenticatedException() {
		super(null, null, null);
	}

}
