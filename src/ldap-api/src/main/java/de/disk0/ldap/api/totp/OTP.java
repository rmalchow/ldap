package de.disk0.ldap.api.totp;

import java.util.Date;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

public class OTP {

	public enum HASH {
		MD5, SHA1, SHA256, SHA512
	};
	
	public enum TYPE { 
			TOTP, HOTP
	}

	private TYPE type = TYPE.TOTP;
	private String id;
	private String url;
	private String secret;
	private String secretHex;
	private String issuer;
	private Date issued;
	private String name;
	private String image;
	private int length = 6;
	private int interval = 30;
	private int offset = 0;
	private long currentTime = 0;
	private long counter = 0;
	private HASH hash = HASH.SHA1;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret.replace("\\s+", " ");
		this.secretHex = Hex.encodeHexString(new Base32().decode(this.secret));
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public HASH getHash() {
		return hash;
	}

	public void setHash(HASH hash) {
		this.hash = hash;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Date getIssued() {
		return issued;
	}

	public void setIssued(Date issued) {
		this.issued = issued;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	public String getSecretHex() {
		return secretHex;
	}

	public void setSecretHex(String secretHex) throws DecoderException {
		this.secretHex = secretHex.replaceAll("[^0-9A-F]+", "");
		this.secret = new Base32().encodeAsString(Hex.decodeHex(this.secretHex.toCharArray()));
	}

}