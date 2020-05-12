package de.disk0.ldap.impl.services.repos;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import de.disk0.dbutil.impl.AbstractGuidRepository;
import de.disk0.dbutil.impl.SimpleQuery;
import de.disk0.ldap.api.entities.LdapEntry;
import de.disk0.ldap.api.entities.GlobalAdmin;
import de.disk0.ldap.api.entities.session.User;

@Component
@DependsOn({"flyway", "flywayInitializer"})
public class GlobalAdminRepo extends AbstractGuidRepository<GlobalAdmin> {

	private static Log log = LogFactory.getLog(GlobalAdminRepo.class);
	
	public void addAdmin(LdapEntry e) {
		SimpleQuery sq = new SimpleQuery("INSERT INTO admin VALUES (:id,:displayname) ON DUPLICATE KEY UPDATE display_name=:displayname");
		sq.put("id", e.getId());
		sq.put("displayname", e.getDisplayname());
		getTemplate().update(sq.getQuery(), sq.getParams());
	}

	public void removeAdmin(LdapEntry e) {
		SimpleQuery sq = new SimpleQuery("DELETE FROM admin WHERE id = :id");
		sq.put("id", e.getId());
		getTemplate().update(sq.getQuery(), sq.getParams());
	}

	public boolean isAdmin(User user) {
		try {
			SimpleQuery sq = new SimpleQuery("SELECT * FROM admin WHERE id IN :ids");
			sq.put("ids",user.getPrincipalIds());
			return find(sq.getQuery(),sq.getParams()).size() > 0;
		} catch (Exception e) {
			log.error("error checking for admins: ",e);
		}
		return false;
	}
	

	

}
