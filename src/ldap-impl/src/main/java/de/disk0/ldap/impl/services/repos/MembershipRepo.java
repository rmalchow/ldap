package de.disk0.ldap.impl.services.repos;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import de.disk0.dbutil.api.Comparator;
import de.disk0.dbutil.api.Operator;
import de.disk0.dbutil.api.Select;
import de.disk0.dbutil.api.TableReference;
import de.disk0.dbutil.api.exceptions.SqlException;
import de.disk0.dbutil.impl.AbstractGuidRepository;
import de.disk0.dbutil.impl.SimpleQuery;
import de.disk0.dbutil.impl.mysql.MysqlStatementBuilder;
import de.disk0.ldap.api.entities.LdapEntry;
import de.disk0.ldap.api.entities.Membership;

@Component
@DependsOn({"flyway", "flywayInitializer"})
public class MembershipRepo extends AbstractGuidRepository<Membership> {

	public MembershipRepo() {
	}

	public Membership get(String groupId, String principalId) throws SqlException {
		SimpleQuery sq = new SimpleQuery("SELECT * FROM membership WHERE group_id = :groupId AND principal_id = :principalId");
		sq.put("groupId", groupId);
		sq.put("principalId", principalId);
		return findOne(sq.getQuery(), sq.getParams());
	}
	
	@Override
	protected void beforeSave(Membership m) throws Exception {
		Membership mOld = get(m.getGroupId(),m.getPrincipalId());
		if(mOld!=null) { m.setId(mOld.getId()); }
		m.setUpdated(new Date());
	}

	public List<Membership> get(LdapEntry e) throws SqlException {
		SimpleQuery sq = new SimpleQuery("SELECT * FROM membership WHERE principal_id = :principalId");
		sq.put("principalId", e.getId());
		return find(sq.getQuery(), sq.getParams());
	}
	
	public void deleteObsolete(Date date) {
		SimpleQuery sq = new SimpleQuery("DELETE FROM membership WHERE updated < :date OR updated IS NULL ");
		sq.put("date", date);
		getTemplate().update(sq.getQuery(), sq.getParams());
		
	}

	public List<Membership> list(String groupId, String principalId) throws SqlException {
		Select s = new MysqlStatementBuilder().createSelect();
		TableReference tr = s.fromTable(Membership.class);
		if(groupId!=null) {
			s.condition(Operator.AND,tr.field("group_id"),Comparator.EQ,tr.value(groupId));
		}
		if(principalId!=null) {
			s.condition(Operator.AND,tr.field("principal_id"),Comparator.EQ,tr.value(principalId));
		}
		return find(s.getSql(), s.getParams());
	}
	
}
