package de.disk0.ldap.api.entities.session;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcg.jwt.TokenWriter;

@Component
public class UserTokenWriter extends TokenWriter<User> {
	
	private static Log log = LogFactory.getLog(UserTokenWriter.class);

	private ObjectMapper om = new ObjectMapper();

	@Override
	public Map<String, Object> map(User arg0) {
		try {
			return om.readValue(om.writeValueAsString(arg0), new TypeReference<Map<String,Object>>() {});
		} catch (JsonProcessingException e) {
			log.error("error mapping: ",e);
			return null;
		}
	}

}
