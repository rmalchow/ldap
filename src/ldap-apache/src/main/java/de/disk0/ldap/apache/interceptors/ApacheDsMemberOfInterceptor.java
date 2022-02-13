package de.disk0.ldap.apache.interceptors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.server.core.api.CoreSession;
import org.apache.directory.server.core.api.interceptor.BaseInterceptor;
import org.apache.directory.server.core.api.interceptor.context.AddOperationContext;
import org.apache.directory.server.core.api.interceptor.context.DeleteOperationContext;
import org.apache.directory.server.core.api.interceptor.context.ModifyOperationContext;

import de.disk0.ldap.apache.ApacheDsEmbedded;

public class ApacheDsMemberOfInterceptor extends BaseInterceptor {
	
	private static Log log = LogFactory.getLog(ApacheDsMemberOfInterceptor.class);

	private ApacheDsEmbedded ds;
	
	public ApacheDsMemberOfInterceptor(ApacheDsEmbedded ds) {
		this.ds = ds;
	}
	
	@Override
	public void add(AddOperationContext addContext) throws LdapException {
		next(addContext);
	}

	@Override
	public void delete(DeleteOperationContext deleteContext) throws LdapException {
		Entry e = deleteContext.getEntry();
		next(deleteContext);
		
		update(e.getDn().toString(),"memberOf","member",e,null);
		update(e.getDn().toString(),"member","memberOf",e,null);
		
	}

	@Override
	public void modify(ModifyOperationContext modifyContext) throws LdapException {
		
		Entry e = modifyContext.getEntry();
		
		next(modifyContext);
		
		Entry me = modifyContext.getAlteredEntry();
		
		update(e.getDn().toString(),"memberOf","member",e,me);
		update(e.getDn().toString(),"member","memberOf",e,me);
		
	}
	
	
	private void update(String target, String attId, String complement, Entry entryBefore, Entry entryAfter) {

		
		List<String> before = new ArrayList<String>();
		List<String> after = new ArrayList<String>();

		if(entryBefore==null) {
			log.info("memberOf interceptor: update: before: entry is NULL ... ");
		} else if(entryBefore.get(complement)==null) {
			log.info("memberOf interceptor: update: before: entry has no "+complement+" / "+entryBefore.get(complement));
		} else {
			log.info("memberOf interceptor: update: before: "+complement);
			Iterator<Value<?>> vs = entryBefore.get(complement).iterator();
			while(vs.hasNext()) {
				String s = vs.next().toString();
				log.info("memberOf interceptor: update: before: "+complement+" = "+s);
				before.add(s);
			}
		}
		
		if(entryAfter==null) {
			log.info("memberOf interceptor: update: after: entry is NULL ... ");
		} else if(entryAfter.get(complement)==null) {
			log.info("memberOf interceptor: update: after: entry has no "+complement);
		} else {
			log.info("memberOf interceptor: update: after: "+complement);
			Iterator<Value<?>> vs = entryAfter.get(complement).iterator();
			while(vs.hasNext()) {
				String s = vs.next().toString();
				log.info("memberOf interceptor: update: after: "+complement+" = "+s);
				after.add(s);
			}
		}
		
		
		
		List<String> unchanged = new ArrayList<String>(before);
		unchanged.retainAll(after);
		
		before.removeAll(unchanged);
		after.removeAll(unchanged);
		
		CoreSession cs = ds.getAdminSession();
		
		log.info("memberOf interceptor: update: unchanged -- : "+unchanged.size());
		log.info("memberOf interceptor: update: removing --- : "+before.size());
		log.info("memberOf interceptor: update: adding ----- : "+after.size());
		
		for(String rm : before) {
			try {
				log.info("memberOf interceptor: update: removing: "+attId+" / from: "+rm);
				Entry e = cs.lookup(new Dn(rm), attId);
				if(!e.containsAttribute(attId)) continue;
				Attribute a = e.get(attId);
				a.remove(target);
				if(a.size()==0) {
					cs.modify(e.getDn(), Collections.singletonList(new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE, a)));
				} else {
					cs.modify(e.getDn(), Collections.singletonList(new DefaultModification(ModificationOperation.REPLACE_ATTRIBUTE, a)));
				}
			} catch (Exception e) {
				log.warn("unable to remove "+attId+" from "+rm);
			}
		}
		

		for(String add : after) {
			try {
				log.info("memberOf interceptor: update: adding: "+attId+" / to: "+add);
				Entry e = cs.lookup(new Dn(add), attId);
				Attribute a = new DefaultAttribute(attId);
				ModificationOperation op = ModificationOperation.ADD_ATTRIBUTE;
				if(e.containsAttribute(attId)) {
					op = ModificationOperation.REPLACE_ATTRIBUTE;
					a = e.get(attId);
				}
				a.add(target);
				cs.modify(e.getDn(), Collections.singletonList(new DefaultModification(op, a)));
			} catch (Exception e) {
				log.warn("unable to remove "+attId+" from "+add);
			}
		}
		
		
		
	}

}
