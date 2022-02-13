package de.disk0.ldap.apache.config;

import java.util.List;

public class ApacheDsPartitionConfig {

	private String name;
	private String dn;
	private String organization;
	private List<String> units;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public List<String> getUnits() {
		return units;
	}

	public void setUnits(List<String> units) {
		this.units = units;
	}
	
}
