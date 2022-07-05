package com.partner.service.impl;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.partner.entity.BpiConfig;
import com.partner.entity.EmailLog;
import com.partner.repository.EmailLogRepository;
import com.partner.service.SendMail;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerConstants;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class SendMailImpl implements SendMail{

	private JavaMailSender javaMailSender;
	private EmailLogRepository emailLogRepository;
	
	@Autowired
	private CommonUtil commonUtil;
	
	public SendMailImpl(JavaMailSender javaMailSender,
			EmailLogRepository emailLogRepository) {
		this.javaMailSender = javaMailSender;
		this.emailLogRepository = emailLogRepository;
	}
	
	private String result=null;
	
	@Async
	@Override
	public String sendEmail(String toEmail,String subject, String body,String type) {
		try {
			log.info("Initiated email sending");
			MimeMessagePreparator messagePreparator = mimeMessage -> {
				MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
				messageHelper.setFrom("support@cintap.com");
				messageHelper.setTo(toEmail);
				messageHelper.setSubject(subject);
				messageHelper.setText(body,true);
			};

			javaMailSender.send(messagePreparator);
			log.info("Email sent successfully");
			//Log email details into database
			EmailLog emailLog = EmailLog.builder()
					.emailAddress(toEmail)
					.emailBody(body)
					.type(type)
					.createdDate(PartnerUtility.getCurrentDateTime())
					.createdBy("SYSTEM")
					.updatedDate(PartnerUtility.getCurrentDateTime())
					.updatedBy("SYSTEM")
					.status(1)
					.build();
			emailLogRepository.save(emailLog);
			result=ResponseCodes.SUCCESS;

		}catch (MailException exception) {
			log.info("LoginController :: Exception :: ",exception.getMessage());
			commonUtil.saveErrorLog(exception);
		}catch (Exception exception1) {
			log.error("Unable to send an email to :"+toEmail+"; Exception is : "+exception1);
			log.info("LoginController :: Exception :: ",exception1.getMessage());
			commonUtil.saveErrorLog(exception1);
			result=ResponseCodes.FAIL;
		}
		return result;
	}
	
	
	
	@Override
	public String sendCustomerEmail(String from,String toEmail,String subject, String body,String type) {
		try {
			log.info("Initiated email sending");
			MimeMessagePreparator messagePreparator = mimeMessage -> {
				MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
				messageHelper.setFrom(from);
				messageHelper.setTo(toEmail);
				messageHelper.setSubject(subject);
				messageHelper.setText(body,true);
			};

			javaMailSender.send(messagePreparator);
			log.info("Email sent successfully :: "+toEmail);
			//Log email details into database
			EmailLog emailLog = EmailLog.builder()
					.emailAddress(toEmail)
					.emailBody(body)
					.type(type)
					.createdDate(PartnerUtility.getCurrentDateTime())
					.createdBy("SYSTEM")
					.updatedDate(PartnerUtility.getCurrentDateTime())
					.updatedBy("SYSTEM")
					.status(1)
					.build();
			emailLogRepository.save(emailLog);
			result=ResponseCodes.SUCCESS;

		}catch (MailException exception) {
			log.info("LoginController :: Exception :: ",exception.getMessage());
			commonUtil.saveErrorLog(exception);
		}catch (Exception exception1) {
			log.error("Unable to send an email to :"+toEmail+"; Exception is : "+exception1);
			log.info("LoginController :: Exception :: ",exception1.getMessage());
			commonUtil.saveErrorLog(exception1);
			result=ResponseCodes.FAIL;
		}
		return result;
	}


	@Async
	@Override
	public String sendEmailToSales(String body, String subject) {
		try {
			log.info("Initiated email sending");
			BpiConfig bpiConfig = commonUtil.findBpiConfigByKey(PartnerConstants.SALES_EMAILS);
			if (bpiConfig != null && StringUtils.isNoneEmpty(bpiConfig.getConfigValue())) {
				final String configEmails = String.valueOf(bpiConfig.getConfigValue());
//				emailAddress = emails.split(",");
			
			MimeMessagePreparator messagePreparator = mimeMessage -> {
				MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
				messageHelper.setFrom("support@cintap.com");
//				messageHelper.setTo(new String[]{"sales@cintap.com", "ansar@cintap.com"});
				messageHelper.setTo(configEmails.split(","));
				messageHelper.setSubject(subject);
				messageHelper.setText(body, true);
			};

			javaMailSender.send(messagePreparator);
			log.info("Email sent successfully");
			// Log email details into database
//			EmailLog emailLog = EmailLog.builder()
//					.emailAddress(toEmail)
//					.emailBody(body)
//					.type(type)
//					.createdDate(PartnerUtility.getCurrentDateTime())
//					.createdBy("SYSTEM")
//					.updatedDate(PartnerUtility.getCurrentDateTime())
//					.updatedBy("SYSTEM")
//					.status(1)
//					.build();
//			emailLogRepository.save(emailLog);
			result = ResponseCodes.SUCCESS;
			}
		} catch (MailException exception) {
			log.info("LoginController :: Exception :: ", exception.getMessage());
			commonUtil.saveErrorLog(exception);
		} catch (Exception exception1) {
			log.error("Unable to send an email to sales team; Exception is : " + exception1);
			log.info("LoginController :: Exception :: ", exception1.getMessage());
			commonUtil.saveErrorLog(exception1);
			result = ResponseCodes.FAIL;
		}
		return result;
	}
}
