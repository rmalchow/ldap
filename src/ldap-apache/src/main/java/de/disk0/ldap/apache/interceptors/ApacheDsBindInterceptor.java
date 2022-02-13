package de.disk0.ldap.apache.interceptors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.server.core.api.interceptor.BaseInterceptor;
import org.apache.directory.server.core.api.interceptor.context.BindOperationContext;

import de.disk0.ldap.apache.ApacheDsEmbedded;

public class ApacheDsBindInterceptor extends BaseInterceptor {
	
	private static Log log = LogFactory.getLog(ApacheDsBindInterceptor.class);

	private ApacheDsEmbedded ds;
	private String name;
	
	public ApacheDsBindInterceptor(ApacheDsEmbedded ds) {
		this.ds = ds;
	}
	
	@Override
	public void bind(BindOperationContext bindContext) throws LdapException {
		/**
		try {
			String s = bindContext.getDn().toString();
			SearchRequest sr = new SearchRequestImpl();
			sr.setScope(SearchScope.SUBTREE);
			sr.setBase(new Dn());
			OrFilter of = new OrFilter();
			of.append(new EqualsFilter("userPrincipalName", s));
			of.append(new EqualsFilter("uid", s));
			sr.setFilter(of.encode());
			
			for(Entry e : ds.getAdminSession().search(sr)) {
				Dn s2 = e.getDn();
				log.info("bind interceptor: dn: "+s+" --> "+s2);
				bindContext.setDn(s2);
				break;
			}
			log.info("bind interceptor: dn: "+s);
		} catch (Exception e) {
		}
		**/
		byte[] creds = bindContext.getCredentials(); 
		if(creds == null || creds.length==0) {
			// try anonymous
			
		}
		
		log.error(" >>>> "+name);
		next(bindContext);
		log.error(" <<<< "+name);
	}
	
	
}
