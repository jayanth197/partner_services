package com.partner.service;

public interface SmsService {
	String sendTwilioSMS(String mobileNumber, String body);
}
