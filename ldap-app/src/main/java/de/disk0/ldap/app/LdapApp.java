package de.disk0.ldap.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages={"de.disk0","com.mcg"})
@EnableScheduling
public class LdapApp {

	public static void main(String[] args) throws Exception {
	    SpringApplication.run(LdapApp.class, args);
	}

	
}
