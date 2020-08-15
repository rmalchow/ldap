package de.disk0.ldap.api.utils;

import de.disk0.ldap.api.entities.session.SessionHolder;
import de.disk0.ldap.api.entities.session.User;
import de.disk0.ldap.api.exceptions.AuthException;
import de.disk0.ldap.api.exceptions.NotAuthenticatedException;
import de.disk0.ldap.api.exceptions.NotAuthorizedException;

public class AuthUtils {

	public AuthUtils() {
	}

	public static boolean isLoggedIn() {
		User u = SessionHolder.get();
		if(u==null) return false;
		if(u.getId()==null) return false;
		if(u.isNeedsReset()) return false;
		return u.isLoggedIn();
	}
	
	public static boolean isAdmin() {
		if(!isLoggedIn()) return false;
		return SessionHolder.get().isAdmin();
	}

	public static void checkLoggedIn() throws AuthException {
		if(!isLoggedIn()) throw new NotAuthenticatedException();
	}
	
	public static void checkAdmin() throws AuthException {
		checkLoggedIn();
		if(!isAdmin()) throw new NotAuthorizedException();
	}
	
	
}
