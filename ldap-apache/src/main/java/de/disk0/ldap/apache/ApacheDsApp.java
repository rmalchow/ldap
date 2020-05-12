package de.disk0.ldap.apache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages={"de.disk0","com.mcg"})
@EnableScheduling
public class ApacheDsApp {

	public static void main(String[] args) throws Exception {
	    SpringApplication.run(ApacheDsApp.class, args);
	}
	
}
