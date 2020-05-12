package de.disk0.ldap.impl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class RegistrationConfig {

	private boolean enabled;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
