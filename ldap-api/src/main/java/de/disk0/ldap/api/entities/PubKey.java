package de.disk0.ldap.api.entities;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.persistence.Column;
import javax.persistence.Table;

import de.disk0.dbutil.api.entities.BaseGuidEntity;

@Table(name = "pub_key")
public class PubKey extends BaseGuidEntity {

	@Column
	private long serial;
	@Column
	private String algorithm;
	@Column
	private String key;

	public long getSerial() {
		return serial;
	}

	public void setSerial(long serial) {
		this.serial = serial;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public static PubKey create(PublicKey key, long serial) {
		PubKey out = new PubKey();
		out.setAlgorithm(key.getAlgorithm());
		out.setKey(Base64.getEncoder().encodeToString(key.getEncoded()));
		out.setSerial(serial);
		return out;
	}
		
		
	public static PublicKey create(PubKey key) {
		try {
			X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(key.getKey()));
			KeyFactory kf = KeyFactory.getInstance(key.getAlgorithm());		
			return kf.generatePublic(spec); 
		} catch (Exception e) {
			throw new RuntimeException("unreadable public key");
		}
	}
	
	
	
	
}
