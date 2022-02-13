package de.disk0.ldap.api.entities;

import javax.persistence.Column;
import javax.persistence.Table;

import de.disk0.dbutil.api.entities.BaseGuidEntity;

@Table(name = "registration")
public class Registration extends BaseGuidEntity {

	@Column(name = "username")
	private String username;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "familyname")
	private String familyname;
	
	@Column(name = "givename")
	private String givename;
	
	@Column(name = "displayname")
	private String displayname;
	

	
}
