package de.disk0.ldap.impl.services.repos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.disk0.dbutil.api.Aggregate;
import de.disk0.dbutil.api.Comparator;
import de.disk0.dbutil.api.Condition;
import de.disk0.dbutil.api.Field;
import de.disk0.dbutil.api.JoinTable;
import de.disk0.dbutil.api.Operator;
import de.disk0.dbutil.api.Select;
import de.disk0.dbutil.api.TableReference;
import de.disk0.dbutil.api.exceptions.SqlException;
import de.disk0.dbutil.impl.AbstractGuidRepository;
import de.disk0.dbutil.impl.SimpleQuery;
import de.disk0.dbutil.impl.mysql.MysqlStatementBuilder;
import de.disk0.ldap.api.entities.EntryType;
import de.disk0.ldap.api.entities.LdapEntry;
import de.disk0.ldap.api.entities.LdapPermission;
import de.disk0.ldap.api.entities.query.EntryQuery;

@Component
@DependsOn({"flyway", "flywayInitializer"})
public class EntryRepo extends AbstractGuidRepository<LdapEntry> {

	private static Log log = LogFactory.getLog(EntryRepo.class);
	
	public Select createSelect(EntryQuery query) throws SqlException {

		Select select = new MysqlStatementBuilder().createSelect();
		TableReference trEntry = select.fromTable("entry");

		if(!query.isIncludeIgnored()) {
			select.condition(Operator.AND,trEntry.field("ignored"),Comparator.EQ,trEntry.value(false));
		}
		
		List<LdapPermission> permissions = query.getPermissions();
		
		permissions = LdapPermission.fillDown(permissions);

		List<String> principalIds = query.getPrincipalIds(); 
		principalIds = principalIds==null?new ArrayList<String>():principalIds;
		
		
		if(permissions.size()==0) {
			log.debug("not imposing any limits ... (no permissions)");
			// not imposing limits
		} else if(principalIds.size()==0) { 
			// not imposing limits
		} else {
			JoinTable trAcl = trEntry.join("entry_acl");
			Condition c1 = trAcl.addOn(Operator.AND);
			c1.condition(Operator.AND, trEntry.field("path"), Comparator.LIKE, trAcl.field("path"));

			c1.condition(Operator.AND, trAcl.field("principal_id"), Comparator.IN, trAcl.value(principalIds));
			List<String> ps = new ArrayList<>();
			for(LdapPermission p : permissions) {
				ps.add(p.name());
			}
			c1.condition(Operator.AND, trAcl.field("permission"), Comparator.IN, trAcl.value(ps));
			if(permissions.contains(LdapPermission.READ)) {
				trAcl.addOn(Operator.OR,trEntry.field("id"),Comparator.IN,trEntry.value(principalIds));
			}
		}
		
		if(!StringUtils.isEmpty(query.getDn())) {
			select.condition(Operator.AND, trEntry.field("dn"), Comparator.LIKE, trEntry.value(query.getDn()));
		}
		
		if(!StringUtils.isEmpty(query.getUsername())) {
			Condition c = select.condition(Operator.AND);
			c.condition(Operator.OR, trEntry.field("name"), Comparator.LIKE, trEntry.value(query.getUsername()));
		}
		
		if(query.getTypes()!=null) {
			List<String> ts = new ArrayList<>();
			for(EntryType t : query.getTypes()) ts.add(t.name());
			select.condition(Operator.AND, trEntry.field("type"), Comparator.IN, trEntry.value(ts));
		}
		
		if(!StringUtils.isEmpty(query.getParentId())) {
			if(query.getParentId().equals("ROOT")) {
				select.isNull(Operator.AND, trEntry.field("parent_id"));
			} else {
				select.condition(Operator.AND, trEntry.field("parent_id"), Comparator.EQ, trEntry.value(query.getParentId()));
			}
		}
		
		if(query.getEntryId()!=null) {
			select.condition(Operator.AND, trEntry.field("id"), Comparator.EQ, trEntry.value(query.getEntryId()));
		}
		
		if(query.getEntryIds()!=null) {
			select.condition(Operator.AND, trEntry.field("id"), Comparator.IN, trEntry.value(query.getEntryIds()));
		}
		
		
		List<String> filters = new ArrayList<>();
		
		if(query.getFilter()==null) {
			// nope
		} else {
			for(String s : query.getFilter().split(",")) {
				s = s.trim();
				if(StringUtils.isBlank(s)) continue;
				filters.add(s.trim());
				Field f = trEntry.value("%"+s+"%");
				Condition c = select.condition(Operator.AND);
				c.condition(Operator.OR, trEntry.field("id"), Comparator.LIKE, f); 
				c.condition(Operator.OR, trEntry.field("display_name"), Comparator.LIKE, f); 
				c.condition(Operator.OR, trEntry.field("name"), Comparator.LIKE, f); 
				c.condition(Operator.OR, trEntry.field("description"), Comparator.LIKE, f); 
			}
		}
		
		select.group(trEntry.field("id"));
		
		String ob = query.getOrder();
		ob = StringUtils.isEmpty(ob)?"name" : ob;
		
		select.order(trEntry.field(Aggregate.UPPER,ob), true);
		select.limit(query.getOffset(), query.getMax());
		
		try {
			log.info("I created a SELECT and this is what i got: \n"+(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(select)));
		} catch (JsonProcessingException e) {
		}
		
		return select;
	}


	@Override
	protected void beforeSave(LdapEntry t) throws Exception {
		
		log.info("entry repo: save: parentId: "+t.getParentId());
		
		super.beforeSave(t);
		if(t.getParentId()==null) {
			SimpleQuery sq = new SimpleQuery("DELETE FROM entry WHERE parent_id IS NULL AND id != :id");
			t.setUpdated(new Date());
			sq.put("id", t.getId());
			getTemplate().update(sq.getQuery(), sq.getParams());
		}
	}
	
	@Override
	public LdapEntry save(LdapEntry t) throws SqlException {
		try {
			beforeSave(t);
		} catch (Exception e) {
			throw new SqlException("save failed",e);
		}
		t.setUpdated(new Date());

		LdapEntry ex = get(t.getId());
		if(ex==null) {
			String id = t.getId();
			t.setId(null);
			return super.save(t,id);
		} else {
			t.setIgnored(ex.isIgnored());
			return super.save(t);
		}
	}

	public void deleteObsolete(Date date) {
		SimpleQuery sq = new SimpleQuery("DELETE FROM entry WHERE updated < :date OR updated IS NULL");
		sq.put("date", date);
		getTemplate().update(sq.getQuery(), sq.getParams());
		
	}


	public List<LdapEntry> list(EntryQuery eq) throws SqlException {
		Select s = createSelect(eq);
		return find(s.getSql(), s.getParams());
	}


	public LdapEntry findOne(EntryQuery eq) throws SqlException {
		Select s = createSelect(eq);
		log.info(s.getSql());
		log.info(eq.getDn());
		return findOne(s.getSql(), s.getParams());
	}


	public void setIgnore(String path, boolean ignore) {
		SimpleQuery sq = new SimpleQuery("UPDATE entry SET ignored = :ignore WHERE path LIKE :path ");
		sq.put("path", path+"%");
		sq.put("ignore", ignore);
		getTemplate().update(sq.getQuery(), sq.getParams());
	}


	public boolean checkName(String name) throws SqlException {
		SimpleQuery sq = new SimpleQuery("SELECT * FROM entry WHERE name LIKE :name");
		sq.put("name",name);
		for(LdapEntry e : find(sq.getQuery(), sq.getParams())) {
			return false;
		}
		return true;
	}
	
	public LdapEntry getByUsername(String username) throws SqlException {
		Select s = new MysqlStatementBuilder().createSelect();
		TableReference tr = s.fromTable(LdapEntry.class);
		s.condition(Operator.AND,tr.field("type"),Comparator.EQ,tr.value("USER"));
		Condition c = s.condition(Operator.AND);
		c.condition(Operator.OR,tr.field("name"), Comparator.LIKE, tr.value(username)); 
		c.condition(Operator.OR,tr.field("email"), Comparator.LIKE, tr.value(username));
		s.limit(0, 1);
		return findOne(s);
	}
	
	
}
