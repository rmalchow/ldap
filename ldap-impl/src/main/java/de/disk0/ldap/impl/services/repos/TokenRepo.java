package de.disk0.ldap.impl.services.repos;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

import de.disk0.dbutil.api.exceptions.SqlException;
import de.disk0.dbutil.impl.AbstractGuidRepository;
import de.disk0.ldap.api.entities.ResetToken;
import de.disk0.ldap.api.utils.TokenGenerator;

@Component
public class TokenRepo extends AbstractGuidRepository<ResetToken> {

	public ResetToken create(String userId) throws SqlException {
		ResetToken out = get(userId);
		if(out == null) {
			out = new ResetToken();
		}
		out.setUserId(userId);
		out.setToken(TokenGenerator.generate(8));
		out.setExpires(DateUtils.addHours(new Date(), 12));
		if(out.getId()!=null) {
			return save(out);
		} else {
			return save(out,userId);
		}
	}

	public boolean hasToken(String userId) throws SqlException {
		ResetToken rt = get(userId);
		if(rt == null) {
			return false;
		}
		if(rt.getExpires().before(new Date())) {
			return false;
		}
		return true;
	}
	
	public boolean verify(String userId, String token) throws SqlException {
		ResetToken rt = get(userId);
		if(rt == null) {
			return false;
		}
		if(rt.getExpires().before(new Date())) {
			return false;
		}
		return rt.getToken().equals(token);
	}
	
	
}
