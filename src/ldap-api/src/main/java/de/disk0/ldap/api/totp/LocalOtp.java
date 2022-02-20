package de.disk0.ldap.api.totp;

import javax.persistence.Column;
import javax.persistence.Table;

import de.disk0.dbutil.api.entities.BaseGuidEntity;

@Table(name = "otp")
public class LocalOtp extends BaseGuidEntity {

	@Column(name = "secret")
	private String secret;
	
	@Column(name = "algorithm")
	private String hashAlgorithm;

	@Column(name = "interval")
	private int interval;

	@Column(name = "digits")
	private int digits;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getDigits() {
		return digits;
	}

	public void setDigits(int digits) {
		this.digits = digits;
	}

	public String getHashAlgorithm() {
		return hashAlgorithm;
	}

	public void setHashAlgorithm(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}


}
