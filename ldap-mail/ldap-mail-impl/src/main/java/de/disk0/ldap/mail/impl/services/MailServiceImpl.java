package de.disk0.ldap.mail.impl.services;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import de.disk0.ldap.api.entities.LdapConfig;
import de.disk0.ldap.mail.api.MailService;
import de.disk0.ldap.mail.api.entities.Mail;
import de.disk0.ldap.mail.api.entities.MailTemplate;
import de.disk0.ldap.mail.impl.repos.MailRepo;


@Service
public class MailServiceImpl implements MailService {
	
	private static Log log = LogFactory.getLog(MailServiceImpl.class);
	
	@Value("${spring.mail.sender:me@example.com}")
	private String sender;
	
	@Autowired
    private JavaMailSender javaMailSender;

	@Autowired
    private LdapConfig config;

	@Autowired
	private MailRepo mailRepo;
	
	private static VelocityEngine ve = new VelocityEngine();
	
	public MailServiceImpl() {
	}
	
	private String runTemplate(Context ctx, String template) {
		if(template == null) {
			return null;
		}
		StringWriter w = new StringWriter();
		ve.evaluate(ctx, w, "", new StringReader(template));
		return w.toString();
	}

	private Mail render(MailTemplate mailTemplate, Map<String, Object> params) {
		
		VelocityContext ctx = new VelocityContext();
		for(Map.Entry<String, Object> e : params.entrySet()) {
			ctx.put(e.getKey(), e.getValue() != null ? e.getValue()  : " ");
		}
		ctx.put("config",config);
		
		Mail out = new Mail();
		
		out.setSubject(runTemplate(ctx, mailTemplate.getSubject()));
		out.setText(runTemplate(ctx, mailTemplate.getText()));

		return out;

	}
	
	@Override
	public boolean sendMail(String recipient, String id, Locale locale, Map<String, Object> params) throws AddressException {
		
		MimeMessage mm = javaMailSender.createMimeMessage();

		try {
			MailTemplate t = mailRepo.getTemplate(id);
			Mail m = render(t, params);
			log.info("sending email: { template: "+id+", sender: "+sender+", recipient: "+recipient+"} ");
			
			mm.setSender(new InternetAddress(sender));
			mm.setRecipient(RecipientType.TO, new InternetAddress(recipient));
			mm.setSubject(m.getSubject(), "utf-8");
			
			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setContent(m.getText(),"text/plain;charset=UTF-8");
			
			MimeMultipart mmp = new MimeMultipart();
			mmp.addBodyPart(mbp);
			
			mm.setContent(mmp);
			
			javaMailSender.send(mm);
			
			return true;
			
		} catch (Exception e) {
			log.error("error sending mail: ",e);
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				mm.writeTo(baos);
				log.info("mail: "+new String(baos.toByteArray(),"utf-8"));
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return false;
		
 	}

}