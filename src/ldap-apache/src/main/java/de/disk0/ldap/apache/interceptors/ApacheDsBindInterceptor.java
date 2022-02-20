package de.disk0.ldap.apache.interceptors;

import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.server.core.api.interceptor.BaseInterceptor;
import org.apache.directory.server.core.api.interceptor.context.BindOperationContext;

import de.disk0.ldap.apache.ApacheDsEmbedded;
import de.disk0.ldap.api.totp.TotpCallbackRegistry;

public class ApacheDsBindInterceptor extends BaseInterceptor {
	
	private static Log log = LogFactory.getLog(ApacheDsBindInterceptor.class);

	private ApacheDsEmbedded ds;

	public ApacheDsBindInterceptor(ApacheDsEmbedded ds) {
		this.ds = ds;
	}
	
	@Override
	public void bind(BindOperationContext bindContext) throws LdapException {
		
		byte[] creds = bindContext.getCredentials(); 
		if(creds == null || creds.length==0) {
			// try anonymous
		} else {
			try {
				String pw = new String(bindContext.getCredentials(),"utf-8");
				pw = TotpCallbackRegistry.callback(bindContext.getDn().toString(), pw);
				if(pw!=null) {
					bindContext.setCredentials(pw.getBytes("utf-8"));
				}
			} catch (UnsupportedEncodingException e) {
			}
		}
		next(bindContext);
	}
	
	
}
