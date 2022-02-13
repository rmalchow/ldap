package de.disk0.ldap.impl.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class CacheInterceptor implements WebMvcConfigurer, HandlerInterceptor {

	private static Log log = LogFactory.getLog(CacheInterceptor.class);
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this);
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("request: "+request.getRequestURI());
		response.setHeader("Cache-Control", "max-age: 0");
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}
	
}
