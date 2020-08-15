package de.disk0.ldap.mail.impl.repos;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.disk0.ldap.mail.api.entities.MailTemplate;

@Component
public class MailRepo {
	
	private static Log log = LogFactory.getLog(MailRepo.class);

	private List<MailTemplate> templates = new ArrayList<MailTemplate>();
	
	public List<MailTemplate> getTemplates() {
		if(templates == null || templates.size()==0) {
			try {
				log.info("mail template: loading ... ");
				ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
				ResourceLoader resourceLoader = new DefaultResourceLoader();
				Resource resource = resourceLoader.getResource("classpath:mail/mail_templates.yaml");
				templates = mapper.readValue(resource.getInputStream(), new TypeReference<List<MailTemplate>>() {});
			} catch (Exception e) {
				log.error("error loading mail templates",e);
			}
		}
		return templates;
	}

	public MailTemplate getTemplate(String id) {
		for(MailTemplate mt : getTemplates()) {
			log.info("mail template: "+mt.getId());
			if(mt.getId().equalsIgnoreCase(id)) {
				return mt;
			}
		}
		log.info("mail template: NOT FOUND");
		return null;
	}

	
	
	
	
}
