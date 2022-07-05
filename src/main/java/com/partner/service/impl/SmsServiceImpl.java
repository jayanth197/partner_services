package com.partner.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partner.config.TwilioConfigProperties;
import com.partner.service.SmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import lombok.extern.slf4j.Slf4j; 

@Service
@Slf4j
public class SmsServiceImpl implements SmsService{
	@Autowired
	TwilioConfigProperties twilioConfigProperties;
	
	// Find your Account Sid and Token at twilio.com/console 
	//public static final String ACCOUNT_SID = "ACabcbfdc6945192471e6600d54e941851"; 
	//public static final String AUTH_TOKEN = "a067b9a697e205a7f36b53c37bc8854c"; 
	public String sendTwilioSMS(String mobileNumber, String body) { 
		log.info("SmsService : sendTwilioSMS - Received request to send sms to : "+mobileNumber+" Body: "+body); 
		Twilio.init(twilioConfigProperties.twilioAccountSid, twilioConfigProperties.twilioAuthToken); 
		Message message = Message.creator( 
				new com.twilio.type.PhoneNumber("91"+mobileNumber),  
				twilioConfigProperties.twilioMsgServiceId, 
				body)      
				.create(); 
		log.info("SmsService : sendTwilioSMS - sent SMS successfully {}",message.getSid());
		return message.getSid();

	} 

}
