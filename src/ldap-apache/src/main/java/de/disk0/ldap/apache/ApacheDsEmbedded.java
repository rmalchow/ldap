package de.disk0.ldap.apache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.schema.extractor.SchemaLdifExtractor;
import org.apache.directory.api.ldap.schema.loader.LdifSchemaLoader;
import org.apache.directory.api.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.api.util.exception.Exceptions;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.api.CoreSession;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.DnFactory;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.api.interceptor.context.AddOperationContext;
import org.apache.directory.server.core.api.interceptor.context.ModifyOperationContext;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.api.schema.SchemaPartition;
import org.apache.directory.server.core.hash.PasswordHashingInterceptor;
import org.apache.directory.server.core.hash.Ssha256PasswordHashingInterceptor;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.server.xdbm.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import de.disk0.ldap.apache.config.ApacheDsConfig;
import de.disk0.ldap.apache.config.ApacheDsListenerConfig;
import de.disk0.ldap.apache.config.ApacheDsPartitionConfig;
import de.disk0.ldap.apache.interceptors.ApacheDsBindInterceptor;
import de.disk0.ldap.apache.interceptors.ApacheDsMemberOfInterceptor;
import de.disk0.ldap.apache.utils.CustomSchemaLdifExtractor;
import de.disk0.ldap.apache.utils.LdapUtil;
import de.disk0.ldap.api.totp.TotpCallback;

@Service
public class ApacheDsEmbedded {

	private static Log log = LogFactory.getLog(ApacheDsEmbedded.class);

	@Value(value = "${ldap.config.allowAnonymous:false}")
	boolean allowAnonymous = false;
	
	@Autowired
	private ApacheDsConfig config;

	private DirectoryService service;
	private List<LdapServer> servers = new ArrayList<LdapServer>();

	private List<ApacheDsPartition> partitions = new ArrayList<ApacheDsPartition>();

	@Autowired
	private ResourceLoader resourceLoader;
	
	public ApacheDsEmbedded() throws Exception {
	}

	@Bean
	public CoreSession getAdminSession() {
		return service.getAdminSession();
	}

	public List<ApacheDsPartition> getPartitions() {
		return partitions;
	}

	public ApacheDsConfig getConfig() {
		return config;
	}

	private Partition addPartition(String partitionId, String partitionDn, DnFactory dnFactory) throws Exception {
		JdbmPartition partition = new JdbmPartition(service.getSchemaManager(), dnFactory);
		partition.setId(partitionId);
		partition.setPartitionPath(new File(service.getInstanceLayout().getPartitionsDirectory(), partitionId).toURI());
		partition.setSuffixDn(new Dn(partitionDn));
		service.addPartition(partition);
		return partition;
	}

	private void addIndex(Partition partition, String... attrs) {
		Set<Index<?, String>> indexedAttributes = new HashSet<>();
		for (String attribute : attrs)
			indexedAttributes.add(new JdbmIndex<String>(attribute, false));
		((JdbmPartition) partition).setIndexedAttributes(indexedAttributes);
	}
	
	private void initSchemaPartition() throws Exception {
		InstanceLayout instanceLayout = service.getInstanceLayout();

		File schemaPartitionDirectory = new File(instanceLayout.getPartitionsDirectory(), "schema");

		// Extract the schema on disk (a brand new one) and load the registries
		if (schemaPartitionDirectory.exists()) {
			log.info("schema partition already exists, skipping schema extraction");
		} else {
			log.info("#### schema partition does not exist, extracting ... ");
			System.setProperty("schema.resource.location", "customer_schema");
			SchemaLdifExtractor extractor = new CustomSchemaLdifExtractor(instanceLayout.getPartitionsDirectory());
			extractor.extractOrCopy(true);
			log.info("#### schema partition does not exist, extracting ... done!");
		}

		LdifSchemaLoader lsl = new LdifSchemaLoader(schemaPartitionDirectory);
		
		SchemaManager schemaManager = new DefaultSchemaManager(lsl);
				
		schemaManager.enable("core","microsoft","nis");

		// We have to load the schema now, otherwise we won't be able
		// to initialize the Partitions, as we won't be able to parse
		// and normalize their suffix Dn
		schemaManager.loadAllEnabled();

		List<Throwable> errors = schemaManager.getErrors();

		if (errors.size() != 0) {
			throw new Exception(I18n.err(I18n.ERR_317, Exceptions.printErrors(errors)));
		}

		service.setSchemaManager(schemaManager);

		// Init the LdifPartition with schema
		LdifPartition schemaLdifPartition = new LdifPartition(schemaManager, service.getDnFactory());
		schemaLdifPartition.setPartitionPath(schemaPartitionDirectory.toURI());

		// The schema partition
		SchemaPartition schemaPartition = new SchemaPartition(schemaManager);
		schemaPartition.setWrappedPartition(schemaLdifPartition);
		service.setSchemaPartition(schemaPartition);
	}
	    

	@PostConstruct
	public void initDirectoryService() throws Exception {

		File workDir = new File(config.getRoot()).getAbsoluteFile();

		service = new DefaultDirectoryService();
		service.setAllowAnonymousAccess(allowAnonymous);
		service.setInstanceId("ADS");
		service.setInstanceLayout(new InstanceLayout(workDir));

		initSchemaPartition();
		
		JdbmPartition systemPartition = new JdbmPartition(service.getSchemaManager(), service.getDnFactory());
		systemPartition.setId("system");

		File systemPath = new File(service.getInstanceLayout().getPartitionsDirectory(), systemPartition.getId());
		systemPartition.setPartitionPath(systemPath.toURI());
		systemPartition.setSuffixDn(new Dn(ServerDNConstants.SYSTEM_DN));
		systemPartition.setSchemaManager(service.getSchemaManager());
		service.setSystemPartition(systemPartition);

		service.getChangeLog().setEnabled(false);
		service.setDenormalizeOpAttrsEnabled(true);

		for (ApacheDsPartitionConfig c : config.getPartitions()) {
			log.info("initializing partition: " + c.getName() + " / " + c.getDn());
			Partition p = addPartition(c.getName(), c.getDn(), service.getDnFactory());
			addIndex(p, "objectClass", "ou", "uid", "cn");

			ApacheDsPartition adp = new ApacheDsPartition();
			adp.setConfig(c);
			adp.setPartition(p);
			partitions.add(adp);
		}

		
		
		service.addFirst(new ApacheDsBindInterceptor(this));
		
		service.addLast(new ApacheDsMemberOfInterceptor(this));

		PasswordHashingInterceptor phi = new Ssha256PasswordHashingInterceptor() {

			@Override
			public void add(AddOperationContext addContext) throws LdapException {
				super.add(addContext);	
			}
			
			@Override
			public void modify(ModifyOperationContext modifyContext) throws LdapException {
				super.modify(modifyContext);
			}
			
		};
		phi.init(service);
		service.addLast(phi);
		
		service.startup();
		
		// set admin pw
		try {
			Dn adminDn = new Dn("uid=admin,ou=system");
			Entry adminEntry = service.getAdminSession().lookup(adminDn, "userPassword", "userPrincipalName");
			

			{
				CoreSession cs = service.getAdminSession();
				log.info("admin session: "+(cs==null?"[NULL]":"[OK]"));
				
				
				{
					List<Modification> mis = new ArrayList<Modification>();
					
					Attribute a = new DefaultAttribute("userPassword",  config.getAdminPassword());
					Modification m = null;
					if(adminEntry.containsAttribute("userPassword")) {
						log.info("updating admin password: " + config.getAdminPassword());
						m = new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, a);
					} else {
						log.info("setting admin password: " + config.getAdminPassword());
						m = new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, a);
					}
					mis.add(m);
					cs.modify(adminDn, mis);
				}
				{
					List<Modification> mis = new ArrayList<Modification>();
	
					if(!adminEntry.contains("objectClass", "simulatedMicrosoftSecurityPrincipal")) {
						mis.add(new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, new DefaultAttribute("objectClass","simulatedMicrosoftSecurityPrincipal")));
					}
					cs.modify(adminDn, mis);
				}
				
				{
					List<Modification> mis = new ArrayList<Modification>();
					if(!adminEntry.containsAttribute("userPrincipalName")) {
						log.info("adding userPrincipalName: " + config.getAdminPassword());
						mis.add(new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, new DefaultAttribute("userPrincipalName","admin")));
						mis.add(new DefaultModification(ModificationOperation.ADD_ATTRIBUTE, new DefaultAttribute("sAMAccountName","admin")));
					}
					cs.modify(adminDn, mis);
				}
	

			}

		} catch (Exception e) {
			log.info("setting admin password failed: "+e.getMessage());
		}

		for (ApacheDsPartition adp : partitions) {
			Dn root = new Dn(adp.getConfig().getDn());
			try {
				service.getAdminSession().lookup(root);
			} catch (LdapException lnnfe) {
				Entry entry = service.newEntry(root);
				entry.add("objectClass", "top", "domain", "extensibleObject");
				service.getAdminSession().add(entry);
			}

			Rdn ro = new Rdn("o", LdapUtil.sanitize(adp.getConfig().getOrganization()));
			Dn org = new Dn(ro, root);
			try {
				service.getAdminSession().lookup(org);
			} catch (LdapException lnnfe) {
				Entry entry = service.newEntry(org);
				entry.add("objectClass", "top", "organization", "extensibleObject");
				entry.add("o", adp.getConfig().getOrganization());
				service.getAdminSession().add(entry);
			}
			for (String s : adp.getConfig().getUnits()) {
				Rdn rou = new Rdn("ou", LdapUtil.sanitize(s));
				Dn ou = new Dn(rou, org);
				log.info("looking up ou: " + s + " / " + ou);
				try {
					service.getAdminSession().lookup(ou);
					log.info("found ou: " + s + " / " + ou);
				} catch (LdapException lnnfe) {
					log.info("creating up ou: " + s + " / " + ou);
					Entry entry = service.newEntry(ou);
					entry.add("objectClass", "top", "organizationalUnit");
					entry.add("ou", s);
					service.getAdminSession().add(entry);
				}
			}
		}
		startServers();
	}

	
	public void startServers() throws Exception {
		for (ApacheDsListenerConfig lc : config.getListeners()) {
			if (lc.getType() == ApacheDsListenerConfig.TYPE.PLAIN) {
				log.info("CREATING ---> PLAIN <--- LISTENER ON PORT: " + lc.getPort());
				LdapServer server = new LdapServer();
				server.setTransports(new TcpTransport(lc.getPort()));
				server.setDirectoryService(service);
				server.start();
				servers.add(server);
			} else {
				throw new RuntimeException("TLS not suppoerted (yet)");
			}
		}
	}

	@EventListener
	public void handleContextClosed(ContextClosedEvent evt) {
		for(LdapServer server : servers) {
			try { 
				server.stop();
			} catch (Exception e) {
			}
		}
		try {
			service.shutdown();
		} catch (Exception e) {
		}
		
	}


}