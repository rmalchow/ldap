package de.disk0.ldap.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class AuthFailedException extends AuthException {

	private static final long serialVersionUID = -5614672867843632597L;

	public AuthFailedException(String code, String message, Throwable cause) {
		super(code, message, cause);
	}

	public AuthFailedException() {
		super(null, null, null);
	}

}
