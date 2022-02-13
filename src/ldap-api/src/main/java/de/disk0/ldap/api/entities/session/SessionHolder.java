package de.disk0.ldap.api.entities.session;

public class SessionHolder {
	
	private static ThreadLocal<User> userHolder = new ThreadLocal<User>();
	
	public static User get() {
		User out = userHolder.get();
		if(out == null) {
			out = new User();
		}
		return out;
	}

	public static void set(User user) {
		userHolder.set(user);
	}

	public static void clear() {
		userHolder.remove();
	}

}
