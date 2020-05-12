package de.disk0.ldap.apache.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration()
@ConfigurationProperties(prefix = "apacheds")
public class ApacheDsConfig {

	private String root;
	private String adminPassword;
	private int port = 3389;
	private List<ApacheDsPartitionConfig> partitions;
	private List<ApacheDsListenerConfig> listeners;

	public List<ApacheDsPartitionConfig> getPartitions() {
		return partitions;
	}

	public void setPartitions(List<ApacheDsPartitionConfig> partitions) {
		this.partitions = partitions;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<ApacheDsListenerConfig> getListeners() {
		return listeners;
	}

	public void setListeners(List<ApacheDsListenerConfig> listeners) {
		this.listeners = listeners;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

}
