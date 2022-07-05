package com.partner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PartnerConfigProperties {

	@Value("${user.default.password}")
	public String defaultPassword;
	
	@Value("${internal.technical.error}")
	public String internalTechnicalError;
	
	@Value("${login.fail.message}")
	public String loginFailMessage;

	@Value("${password.change.success}")
	public String passwordChangeSuccess;

	@Value("${invalid.username}")
	public String invalidUserName;

	@Value("${partner.association.success}")
	public String partnerAssociationSuccess;
	
	@Value("${partner.status.active}")
	public String partnerStatusActive;
	
	@Value("${partner.status.inactive}")
	public String partnerStatusInactive;
	
	@Value("${duplicate.partner}")
	public String duplicatePartner;
	
	@Value("${onboarding.success.message}")
	public String onboardingSuccessMessage;
	
	@Value("${email.duplicate.message}")
	public String emailDuplicateMessage;
	
	@Value("${image.upload.path}")
	public String imageUploadPath;
	
	@Value("${search.partner.fail}")
	public String searchPartnerFail;
	
	@Value("${partner.invitation.success}")
	public String partnerInvitationSuccess;
	
	@Value("${jwt.header.string}")
	public String jwtHeader;
	
	@Value("${jwt.token.prefix}")
	public String jwtTokenPrefix;
	
	@Value("${jwt.signingkey}")
	public String jwtSigningKey;
	
	@Value("${jwt.token.validity.milli.seconds}")
	public String jwtValiditySeconds;
	
	@Value("${external.success}")
	public String externalSuccess;
	
	@Value("${audit.success}")
	public String auditSuccess;
	
	@Value("${key.generate.success}")
	public String keyGeneratedSuccess;
	
	@Value("${apikey.valid}")
	public String apiKeyValid;
	
	@Value("${apikey.invalid}")
	public String apiKeyInValid;

}
