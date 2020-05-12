package de.disk0.ldap.api.entities;

public class Complaint {

	private String code;
	private String message;
	
	public Complaint() {
	}
	
	public Complaint(String code) {
		super();
		this.code = code;
	}

	public Complaint(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
