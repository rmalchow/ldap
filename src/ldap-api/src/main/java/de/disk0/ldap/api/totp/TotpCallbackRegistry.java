package de.disk0.ldap.api.totp;

import java.util.ArrayList;
import java.util.List;

public class TotpCallbackRegistry {

	private static List<TotpCallback> callbacks = new ArrayList<TotpCallback>();
	
	public static void add(TotpCallback c) {
		callbacks.add(c);
	}
	
	public static String callback(String dn, String password) {
		for(TotpCallback tc : callbacks) {
			String pw = tc.callback(dn, password);
			if(pw!=null) {
				return pw;
			}
		}
		return null;
	}
	
}
