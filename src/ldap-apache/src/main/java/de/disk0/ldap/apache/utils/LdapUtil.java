package de.disk0.ldap.apache.utils;

import java.io.UnsupportedEncodingException;

public class LdapUtil {

	public static String sanitize(final String input) {

		String s = "";

		for (int i = 0; i < input.length(); i++) {

			char c = input.charAt(i);

			if (c == '*') {
				s += "\\2a";
			} else if (c == '(') {
				s += "\\28";
			} else if (c == ')') {
				s += "\\29";
			} else if (c == '\\') {
				s += "\\5c";
			} else if (c == '\u0000') {
				s += "\\00";
			} else if (c <= 0x7f) {
				s += String.valueOf(c);
			} else if (c >= 0x080) {
				try {
					byte[] utf8bytes = String.valueOf(c).getBytes("UTF8");

					for (byte b : utf8bytes)
						s += String.format("\\%02x", b);

				} catch (UnsupportedEncodingException e) {
				}
			}
		}
		return s;
	}

}
