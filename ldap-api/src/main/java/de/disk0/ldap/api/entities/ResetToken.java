package de.disk0.ldap.api.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import de.disk0.dbutil.api.entities.BaseGuidEntity;

@Table(name =  "reset")
public class ResetToken extends BaseGuidEntity {
	
	@Column
	private String token;
	
	@Column(name = "user_id")
	private String userId;

	@Column
	private Date expires;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}
	
	

}
