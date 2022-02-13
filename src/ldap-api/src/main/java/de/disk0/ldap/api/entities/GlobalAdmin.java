package de.disk0.ldap.api.entities;

import javax.persistence.Column;
import javax.persistence.Table;

import de.disk0.dbutil.api.entities.BaseGuidEntity;

@Table(name = "admin")
public class GlobalAdmin extends BaseGuidEntity {

	@Column(name = "display_name")
	private String displayname;

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

}
