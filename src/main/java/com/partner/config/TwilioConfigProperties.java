package com.partner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfigProperties {

	@Value("${twilio.account.sid}")
	public String twilioAccountSid;

	@Value("${twilio.auth.token}")
	public String twilioAuthToken;
	
	@Value("${twilio.messaging.service.sid}")
	public String twilioMsgServiceId;
	
	@Value("${2fa.sms.template}")
	public String twoFactorSmsTemplate;
	
	@Value("${changepwd.sms.template}")
	public String changePasswordSmsTemplate;
}
