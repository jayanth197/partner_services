package com.partner.service;

public interface SendMail {
	public String sendEmail(String toEmail,String subject, String body,String type);
	public String sendEmailToSales(String body, String subject);
	public String sendCustomerEmail(String from,String toEmail,String subject, String body,String type);
}
