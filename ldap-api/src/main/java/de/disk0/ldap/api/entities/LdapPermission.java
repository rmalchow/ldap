package de.disk0.ldap.api.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public enum LdapPermission {

	CREATE, 
	CREATE_USER, 
	CREATE_GROUP, 
	READ, 
	WRITE, 
	DELETE, 
	PW_RESET, 
	ACL_READ, 
	ACL_WRITE, 
	EDIT_MEMBERS, 
	SET_USER_STATE,
	ADMIN; 
	
	
	
	public static List<LdapPermission> fillUp(List<LdapPermission> in) {
		SortedSet<LdapPermission> s = new TreeSet<>();
		
		
		if(in.contains(LdapPermission.ADMIN)) {
			s.addAll(Arrays.asList(LdapPermission.values()));
		}
		if(in.contains(LdapPermission.ACL_WRITE)) {
			s.add(LdapPermission.ACL_READ);
			s.add(LdapPermission.READ);
		}
		if(in.contains(LdapPermission.DELETE)) {
			s.add(LdapPermission.WRITE);
		}
		if(in.contains(LdapPermission.CREATE)) {
			s.add(LdapPermission.WRITE);
		}
		if(in.contains(LdapPermission.SET_USER_STATE)) {
			s.add(LdapPermission.READ);
		}
		if(in.contains(LdapPermission.PW_RESET)) {
			s.add(LdapPermission.READ);
		}
		if(in.contains(LdapPermission.WRITE)) {
			s.add(LdapPermission.READ);
		}
		return new ArrayList<LdapPermission>(s);
	}
	
	public static List<LdapPermission> fillDown(List<LdapPermission> in) {
		SortedSet<LdapPermission> s = new TreeSet<>(in);
		s.add(LdapPermission.ADMIN);
		if(in.contains(LdapPermission.READ)) {
			s.addAll(Arrays.asList(LdapPermission.values()));
		}
		if(in.contains(LdapPermission.ACL_READ)) {
			s.add(LdapPermission.ACL_WRITE);
		}
		return new ArrayList<LdapPermission>(s);
	}
	
	
}
