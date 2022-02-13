package de.disk0.ldap.apache;

import org.apache.directory.server.core.api.CoreSession;
import org.apache.directory.server.core.api.partition.Partition;

import de.disk0.ldap.apache.config.ApacheDsPartitionConfig;

public class ApacheDsPartition {

	private Partition partition;
	private CoreSession adminSession;
	private ApacheDsPartitionConfig config;

	public CoreSession getAdminSession() {
		return adminSession;
	}

	public void setAdminSession(CoreSession adminSession) {
		this.adminSession = adminSession;
	}

	public Partition getPartition() {
		return partition;
	}

	public void setPartition(Partition partition) {
		this.partition = partition;
	}

	public ApacheDsPartitionConfig getConfig() {
		return config;
	}
	

	public void setConfig(ApacheDsPartitionConfig config) {
		this.config = config;
	}

}
