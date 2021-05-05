package de.disk0.ldap.impl.services.repos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.disk0.dbutil.api.Comparator;
import de.disk0.dbutil.api.JoinTable;
import de.disk0.dbutil.api.Operator;
import de.disk0.dbutil.api.Select;
import de.disk0.dbutil.api.TableReference;
import de.disk0.dbutil.api.exceptions.SqlException;
import de.disk0.dbutil.impl.AbstractGuidRepository;
import de.disk0.dbutil.impl.SimpleQuery;
import de.disk0.dbutil.impl.mysql.MysqlStatementBuilder;
import de.disk0.ldap.api.entities.EntryAcl;
import de.disk0.ldap.api.entities.LdapEntry;
import de.disk0.ldap.api.entities.LdapPermission;

@Component
@DependsOn({"flyway", "flywayInitializer"})
public class EntryAclRepo extends AbstractGuidRepository<EntryAcl> {
	
	private static Log log = LogFactory.getLog(EntryAclRepo.class);
	
	@Autowired
	private EntryRepo entryRepo;

	public void checkFirst(String id) throws SqlException {
		Select s = new MysqlStatementBuilder().createSelect();
		s.fromTable(EntryAcl.class);
		s.limit(0, 1);
		if(find(s.getSql(),s.getParams()).size()>0) return;
		
		EntryAcl a = new EntryAcl();
		a.setPath("%");
		a.setPrincipalId(id);
		a.setPermission(LdapPermission.ADMIN);
		a.setEntryId("%");
		a.setRecursive(true);
		save(a);
	}
	
	public boolean isAdmin(SortedSet<String> principalIds) throws SqlException {
		Select s = new MysqlStatementBuilder().createSelect();
		TableReference tr = s.fromTable(EntryAcl.class);
		s.condition(Operator.AND,tr.field("principal_id"),Comparator.IN,tr.value(principalIds));
		s.condition(Operator.AND,tr.field("entry_id"),Comparator.EQ,tr.value("%"));
		s.limit(0, 1);
		
		try {
			log.info("EntryACL : is admin? "+new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(s));
		} catch (JsonProcessingException e) {
		}

		boolean out = find(s.getSql(),s.getParams()).size() == 1; 
		log.info("EntryACL : is admin? "+out);
		return out;
	}

	public List<EntryAcl> getEffectiveAcls(String id, List<String> principalIds) throws SqlException {
		Select s = new MysqlStatementBuilder().createSelect();
		TableReference tr = s.fromTable(EntryAcl.class);
		JoinTable jt = tr.join(LdapEntry.class);
		jt.addOn(Operator.AND, jt.field("path"), Comparator.LIKE, tr.field("path"));
		jt.addOn(Operator.AND, tr.field("principal_id"), Comparator.IN, tr.value(principalIds));
		jt.addOn(Operator.AND, jt.field("id"), Comparator.EQ, jt.value(id));
		s.group(tr.field("permission"));
		s.order(tr.field("permission"), true);
		try {
			log.info("I created a SELECT and this is what i got: \n"+(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(s)));
		} catch (JsonProcessingException e) {
		}

		return find(s.getSql(), s.getParams());
	}
	
	public void deleteAcl(String entryId, String aclId) {
		SimpleQuery sq = new SimpleQuery("DELETE FROM entry_acl WHERE entry_id = :entryId AND id = :aclId");
		sq.put("entryId", entryId);
		sq.put("aclId", aclId);
		getTemplate().update(sq.getQuery(), sq.getParams());
	}
	
	public List<EntryAcl> listAcls(String entryId) throws SqlException {
		LdapEntry e = entryRepo.get(entryId);
		SimpleQuery sq = new SimpleQuery("SELECT * FROM entry_acl WHERE (`entry_id` IN (:entryIds) AND `recursive` = :recursive) OR (`entry_id` = :entryId)");
		sq.put("entryIds", Arrays.asList(e.getPath().split("\\.")));
		sq.put("entryId", entryId);
		sq.put("recursive", true);
		return find(sq.getQuery(),sq.getParams());
	}
	

	@Override
	public EntryAcl mapRow(ResultSet arg0, int arg1) throws SQLException {
		EntryAcl e = super.mapRow(arg0, arg1);
		try {
			e.setEntry(entryRepo.get(e.getEntryId()));
			e.setPrincipal(entryRepo.get(e.getPrincipalId()));
		} catch (Exception e2) {
			log.warn("unable to load related entries: ",e2);
		}
		return e;
	}


}
