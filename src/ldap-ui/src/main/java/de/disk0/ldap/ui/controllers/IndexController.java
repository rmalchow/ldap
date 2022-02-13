package de.disk0.ldap.ui.controllers;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class IndexController {

	
	@GetMapping(value = "/")
	private void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String host = request.getHeader("host");
		String proto = request.isSecure()?"https":"http";
		String prefix = "";
		if(request.getHeader("x-forwarded-proto")!=null) {
			proto = request.getHeader("x-forwarded-proto");
		}
		if(request.getHeader("x-forwarded-host")!=null) {
			host = request.getHeader("x-forwarded-host");
		}
		if(request.getHeader("x-forwarded-prefix")!=null) {
			prefix = request.getHeader("x-forwarded-prefix");
		}
		response.sendRedirect(proto+"://"+host+prefix+"/ui");
	}
	@GetMapping(value = "/ui")
	private ModelAndView index() throws IOException {
		ModelAndView out = new ModelAndView();
		out.setViewName("index");
		String v = UUID.randomUUID().toString();
		out.addObject("v", v);
		return out;
	}
	
	
}
