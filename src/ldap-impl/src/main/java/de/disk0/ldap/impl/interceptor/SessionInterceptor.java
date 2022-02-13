package de.disk0.ldap.impl.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.disk0.ldap.api.entities.session.SessionHolder;
import de.disk0.ldap.api.entities.session.User;
import de.disk0.ldap.api.entities.session.UserTokenReader;

@Component
public class SessionInterceptor implements WebMvcConfigurer, HandlerInterceptor {

	private static Log log = LogFactory.getLog(SessionInterceptor.class);
	
	@Autowired
	private UserTokenReader userTokenReader;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this);
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Cookie[] cookies = request.getCookies();
		if(cookies==null) {
			/// meh
			log.info(" --- no cookie ");
		} else {
			log.info(" --- checking cookies ("+cookies.length+")");
			String xsrf = request.getHeader("X-XSRF-TOKEN");
			for(Cookie c : cookies) {
				User user = null; 
				log.info(" --- cookie: "+c.getName());
				if(c.getName().equalsIgnoreCase("JWT")) {
					log.info(" --- jwt cookie ... ");
					try {
						user = userTokenReader.readToken(c.getValue());
					} catch (Exception e) {
					}
				}

				if(user == null) continue;

				SessionHolder.set(user);
				
				if(request.getMethod().equalsIgnoreCase("GET")) {
					log.info(" --- XSRF: its a GET ... ");
					continue; 
				} else {
					log.info(" --- XSRF: its NOT a GET ... ");
					log.info(" --- XSRF: "+user.getXsrf()+" / "+xsrf);
				}

				break;
				
			}
		}
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		SessionHolder.clear();
	}
	
}
