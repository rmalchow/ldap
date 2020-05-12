package de.disk0.ldap.api.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import de.disk0.dbutil.api.entities.BaseGuidEntity;

@Table(name="membership")
public class Membership extends BaseGuidEntity {

	@Column(name="group_id")
	private String groupId;
	
	@Column(name="principal_id")
	private String principalId;
	
	@Column(name="description")
	private String description;

	@Column(name="updated")
	private Date updated;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	
}
