package de.disk0.ldap.impl.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mcg.apitester.api.annotations.ApiSecret;

import de.disk0.dbutil.api.exceptions.SqlException;
import de.disk0.ldap.api.entities.session.SessionHolder;
import de.disk0.ldap.api.entities.session.User;
import de.disk0.ldap.api.entities.session.UserTokenWriter;
import de.disk0.ldap.api.exceptions.LdapException;
import de.disk0.ldap.api.exceptions.NotAuthorizedException;
import de.disk0.ldap.api.services.LdapService;
import io.micrometer.core.instrument.util.StringUtils;

@RestController
@RequestMapping(value = "/api/authenticate")
public class AuthenticationController {

	private Log log = LogFactory.getLog(AuthenticationController.class);
	
	@Autowired
	private LdapService ldapService;
	
	@Autowired
	private UserTokenWriter userTokenWriter;

	private void write(HttpServletResponse response, User user) throws NoSuchAlgorithmException {
		{
			// XSRF 
			Cookie c = new Cookie("XSRF-TOKEN", user.getXsrf());
			c.setHttpOnly(false);
			c.setPath("/");
			c.setMaxAge(180);
			response.addCookie(c);
		}
		{
			// JWT 
			Cookie c = new Cookie("JWT", userTokenWriter.createToken(user, DateUtils.addMinutes(new Date(), 180)));
			c.setHttpOnly(true);
			c.setPath("/");
			c.setMaxAge(180*60);
			response.addCookie(c);
		}
		user.setXsrf(null);
	}
	
	@DeleteMapping
	public User logout(HttpServletResponse response) throws Exception {
		User user = new User();
		write(response, user);
		return user;
	}

	@ResponseStatus(value = org.springframework.http.HttpStatus.NO_CONTENT)
	@PostMapping(value="/password")
	public void updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) throws Exception {
		ldapService.setPassword(SessionHolder.get().getId(), oldPassword, newPassword);
	}

	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@PostMapping(value = "/reset")
	public void reset(
			@RequestParam(required = false) String username
			) {
		ldapService.reset(username);
	}

	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@PostMapping(value = "/update")
	public void updatePassword(
			@RequestParam(required = false) String newPassword
			) throws NotAuthorizedException, SqlException, LdapException {
		ldapService.updatePassword(newPassword);
	}

	
	@PostMapping
	public User authenticate(
			@RequestParam(required = false) String username, 
			@RequestParam(required = false) @ApiSecret String password, 
			@RequestParam(required = false) List<String> groupId, 
			HttpServletResponse response) throws Exception {
		User user = SessionHolder.get();
		log.info("before auth - user is: "+user);
		if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			try {
				user = ldapService.update(user.getId());
			} catch (Exception e) {
				user = new User();
			}
		} else {
			// to allow comma-separated lists 
			List<String> gIds = new ArrayList<String>();
			if(groupId!=null) {
				for(String gs : groupId) {
					for(String g : gs.split(",")) {
						if(g.trim().length() == 0) continue;
						gIds.add(g);
					}
				}
			}
			user = ldapService.authenticate(username, password, gIds); 
		}
		
		
		log.info("after auth - user is: "+user);
		write(response, user);
		return user; 
	}
	
}
