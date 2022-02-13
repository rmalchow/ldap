package de.disk0.ldap.impl.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.disk0.ldap.api.utils.ImageUtil;

@RestController
public class LogoController {
	
	@GetMapping(value = "/api/logo")
	public void logo(HttpServletRequest req, HttpServletResponse res) throws IOException, NoSuchAlgorithmException {
		res.setHeader("Content-Type", "image/png");
		byte[] b = ImageUtil.generateIdenticons(req.getHeader("host"), 60, 60);
		OutputStream os = res.getOutputStream();
		os.write(b);
		os.flush();
	}

}
