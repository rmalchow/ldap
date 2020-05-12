package de.disk0.ldap.api.utils;

import java.security.SecureRandom;

public class TokenGenerator {

	private static String characters = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
	
	private static SecureRandom sr = new SecureRandom();
	
	public static String generate(int length) {
		StringBuffer sb = new StringBuffer();
		
		byte[] buff = new byte[length] ;
		sr.nextBytes(buff);
		
		for(byte b : buff) {
			
			sb.append(characters.charAt(b & 0x1F));
		}
		
		return sb.toString();
	}
	

}
