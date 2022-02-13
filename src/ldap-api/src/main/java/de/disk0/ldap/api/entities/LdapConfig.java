package de.disk0.ldap.api.entities;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ldap.config")
public class LdapConfig {

	private String name;
	
	private String url;
	
	private String username;
	
	private String password;

	private String rootDn;

	private LdapType type;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRootDn() {
		return rootDn;
	}

	public void setRootDn(String rootDn) {
		this.rootDn = rootDn;
	}

	public LdapType getType() {
		return type;
	}

	public void setType(LdapType type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
