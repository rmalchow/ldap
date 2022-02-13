package de.disk0.ldap.mail.api;

import java.util.Locale;
import java.util.Map;

public interface MailService {

	boolean sendMail(String recipient, String id, Locale locale, Map<String, Object> params) throws Exception;

}
