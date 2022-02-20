package de.disk0.ldap.api.totp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class TokenGenerator {

	public static final String GOOD = "ABCDEFGHJKLMNPRSTUVWXYZabcdefghkmnpqrstuvwxyz234567890";

	public static final String UPPER = "ABCDEFGHJKLMNPRSTUVWXYZ";
	public static final String LOWER = "abcdefghkmnpqrstuvwxyz";
	public static final String NUMBERS = "234567890";
	public static final String SPECIAL = "-_.!?#";
	
	public static String generateToken(int length) {
		List<String> out = new ArrayList<>();
		while(out.size()<length) {
			int x = (int)Math.floor(Math.random()*GOOD.length());
			out.add(GOOD.substring(x,x+1));
		}
		return StringUtils.join(out,"");
	}
	
	public static String generatePassword(int length) {
		List<String> b = new ArrayList<>(); 
		String[] candidates = new String[] { UPPER, LOWER, NUMBERS, SPECIAL };
		for(String s : candidates) {
			b.add(s);
		}
		while(b.size() < length) {
			b.add(candidates[(int)Math.floor(Math.random()*(double)candidates.length)]);
		}
		Collections.shuffle(b);
		String out = "";
		for(String s : b) {
			out = out + s.charAt((int)Math.floor(Math.random()*(double)s.length()));
		}
		return out;
	}
	
	public static void main(String[] args) {
		for(int i=0;i < 20; i++) {
			System.err.println(generatePassword(20));
		}
	}
	
	
}