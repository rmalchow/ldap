package de.disk0.ldap.impl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ldap")
public class Config {

	private RegistrationConfig registration = new RegistrationConfig();
	private MailConfig mail = new MailConfig();

	@Bean
	public RegistrationConfig getRegistration() {
		return registration;
	}

	public void setRegistration(RegistrationConfig registration) {
		this.registration = registration;
	}
	
	@Bean
	public MailConfig getMail() {
		return mail;
	}

	public void setMail(MailConfig mail) {
		this.mail = mail;
	}
}
