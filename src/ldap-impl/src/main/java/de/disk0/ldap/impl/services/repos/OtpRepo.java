package de.disk0.ldap.impl.services.repos;

import org.springframework.stereotype.Component;

import de.disk0.dbutil.impl.AbstractGuidRepository;
import de.disk0.ldap.api.totp.LocalOtp;

@Component
public class OtpRepo extends AbstractGuidRepository<LocalOtp> {

}
