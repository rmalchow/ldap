package de.disk0.ldap.impl.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.disk0.ldap.impl.config.RegistrationConfig;

@RestController
@RequestMapping(value = "/api/register")
public class RegistrationController {

	@Autowired
	private RegistrationConfig registrationConfig;
	
	@RequestMapping(value = "/allow")
	public boolean allow() {
		return registrationConfig.isEnabled();
	}
	
}
