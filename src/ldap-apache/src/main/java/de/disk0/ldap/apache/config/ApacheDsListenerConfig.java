package de.disk0.ldap.apache.config;

public class ApacheDsListenerConfig {

	public enum TYPE {
		PLAIN, TLS
	}

	private TYPE type;
	private int port;

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
