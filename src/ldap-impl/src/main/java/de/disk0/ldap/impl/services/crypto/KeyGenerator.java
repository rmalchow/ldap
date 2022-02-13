package de.disk0.ldap.impl.services.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class KeyGenerator {
	
	private static Log log = LogFactory.getLog(KeyGenerator.class);

	private String algorithm = "EC";
	private String ecCurve = "secp384r1";
	
	public KeyGenerator() {
	}

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		log.info("algorithm: "+algorithm);
		if(algorithm.equals("RSA")) return generateRsaKeyPair();
		if(algorithm.equals("EC")) return generateEcKeyPair();
		throw new NoSuchAlgorithmException();
	}
	
	public KeyPair generateRsaKeyPair() {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(4096,new SecureRandom());
			return kpg.generateKeyPair();
		} catch (Exception e) {
			throw new RuntimeException(e); 
		}
	}
	
	public KeyPair generateEcKeyPair() {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
		    kpg.initialize(new ECGenParameterSpec(getEcCurve()),new SecureRandom());
			return kpg.generateKeyPair();
		} catch (Exception e) {
			throw new RuntimeException(e); 
		}
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getEcCurve() {
		return ecCurve;
	}

	public void setEcCurve(String ecCurve) {
		this.ecCurve = ecCurve;
	}
	
}