package de.disk0.ldap.impl.services.crypto;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mcg.jwt.PrivateKeyProvider;
import com.mcg.jwt.PublicKeyProvider;
import com.mcg.jwt.entities.EncodedPrivateKey;
import com.mcg.jwt.entities.EncodedPublicKey;

import de.disk0.dbutil.api.exceptions.SqlException;
import de.disk0.ldap.impl.services.repos.PubKeyRepo;

@Component
public class KeyProvider implements PrivateKeyProvider, PublicKeyProvider {

	private KeyGenerator kg = new KeyGenerator();
	private Map<Long,EncodedPublicKey> publicKeys = new HashMap<>();
	private EncodedPrivateKey encodedPrivateKey;

	@Autowired
	private PubKeyRepo pubKeyRepo;
	
	@PostConstruct
	public void generateKeyPair() throws NoSuchAlgorithmException {
		try {
			KeyPair kp = kg.generateKeyPair();
			
			
			long serial = System.currentTimeMillis();
			
			EncodedPrivateKey eprivk = new EncodedPrivateKey();
			eprivk.setAlgorithm(kg.getAlgorithm());
			eprivk.setSerial(serial);
			eprivk.setPrivateKey(kp.getPrivate());
			
			EncodedPublicKey epubk = new EncodedPublicKey();
			epubk.setAlgorithm(kg.getAlgorithm());
			epubk.setSerial(serial);
			epubk.setPublicKey(kp.getPublic());
			
			pubKeyRepo.save(kp.getPublic(), serial);
			
			publicKeys.put(serial, epubk);
			this.encodedPrivateKey = eprivk;

		} catch (SqlException se) {
			throw new NoSuchAlgorithmException(se);
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException();
		}
	}

	@Override
	public EncodedPublicKey getPublicKey(long serial) throws NoSuchAlgorithmException {
		EncodedPublicKey epk = publicKeys.get(serial);
		if(epk!=null) return epk;
		try {
			PublicKey pk = pubKeyRepo.get(serial);
			if(pk==null) return null;
			
			epk = new EncodedPublicKey();
			epk.setAlgorithm(pk.getAlgorithm());
			epk.setPublicKey(pk);
			epk.setSerial(serial);
			
			return epk;

		} catch (SqlException se) {
			throw new NoSuchAlgorithmException(se);
		}
				
	}

	@Override
	public EncodedPrivateKey getPrivateKey() throws NoSuchAlgorithmException {
		return encodedPrivateKey;
	}
	
	

}