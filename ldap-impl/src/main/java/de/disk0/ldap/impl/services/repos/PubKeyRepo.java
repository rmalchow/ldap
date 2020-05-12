package de.disk0.ldap.impl.services.repos;

import java.security.PublicKey;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import de.disk0.dbutil.api.exceptions.SqlException;
import de.disk0.dbutil.impl.AbstractGuidRepository;
import de.disk0.dbutil.impl.SimpleQuery;
import de.disk0.ldap.api.entities.PubKey;

@Component
@DependsOn({"flyway", "flywayInitializer"})
public class PubKeyRepo extends AbstractGuidRepository<PubKey> {

	public void save(PublicKey pk, long serial) throws SqlException {
		PubKey k = PubKey.create(pk, serial);
		save(k);
	}
	
	public PublicKey get(long serial) throws SqlException {
		SimpleQuery sq = new SimpleQuery("SELECT * FROM pub_key WHERE serial = :serial");
		sq.put("serial", serial);
		PubKey pk = findOne(sq.getQuery(), sq.getParams());
		if(pk==null) return null;
		return PubKey.create(pk);
	}

	
	
	
}
