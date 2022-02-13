package de.disk0.ldap.impl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class MailConfig {

	enum TYPE { PLAIN, START_TLS, TLS };
	
	private boolean enabled;
	private TYPE type;
	private String smtpHost;
	private int smtpPort;
	private String smtpUsername;
	private String smtpPassword;
	private String from;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
}
