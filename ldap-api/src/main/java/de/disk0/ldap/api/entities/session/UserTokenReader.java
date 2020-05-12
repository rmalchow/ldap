package de.disk0.ldap.api.entities.session;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcg.jwt.TokenReader;

@Component
public class UserTokenReader extends TokenReader<User> {

	private static Log log = LogFactory.getLog(UserTokenReader.class);
	
	private ObjectMapper om = new ObjectMapper();
	
	@Override
	public User unmap(Map<String, Object> arg0) {
		try {
			arg0.remove("exp");
			return om.readValue(om.writeValueAsString(arg0), User.class);
		} catch (JsonProcessingException e) {
			log.error("error unmapping: ",e);
			return null;
		}
	}

}
