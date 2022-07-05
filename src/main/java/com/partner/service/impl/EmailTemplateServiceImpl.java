package com.partner.service.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.partner.entity.EmailTemplate;
import com.partner.entity.PartnerInvitation;
import com.partner.entity.PartnerUserDetail;
import com.partner.enums.EMAILTEMPLATENAME;
import com.partner.model.EmailDetails;
import com.partner.repository.EmailTemplateRepository;
import com.partner.service.EmailTemplateService;
import com.partner.service.SendMail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailTemplateServiceImpl implements EmailTemplateService{

	private EmailTemplateRepository emailTemplateRepository;
	private SendMail sendMail;
	
	public EmailTemplateServiceImpl(EmailTemplateRepository emailTemplateRepository,
			SendMail sendMail) {
		this.emailTemplateRepository = emailTemplateRepository;
		this.sendMail = sendMail;
	}
	
	@Override
	public EmailTemplate fetchEmailTemplate(String name) {
		EmailTemplate emailTemplate=null;
		Optional<EmailTemplate> emailTemp = emailTemplateRepository.findByTemplateName(name);
		if(emailTemp.isPresent()) {
			emailTemplate =  emailTemp.get();
		}else {
			log.info("Email Template is not present in the database");
		}
		return emailTemplate;
	}

	@Override
	public void buildWelcomeTemplate(EmailTemplate emailTemplate, PartnerUserDetail partnerUserDetail) {
		String body = emailTemplate.getTemplateBody();

		if(body!=null && !StringUtils.isEmpty(partnerUserDetail.getFirstName()) && 
				!StringUtils.isEmpty(partnerUserDetail.getLastName()) && 
				!StringUtils.isEmpty(partnerUserDetail.getEmailAddress())
				&& !StringUtils.isEmpty(partnerUserDetail.getPassword()))
		{
			body = body.replace("@@FirstName", partnerUserDetail.getFirstName())
					.replace("@@LastName", partnerUserDetail.getLastName())
					.replace("@@LoginUser", partnerUserDetail.getEmailAddress())
					.replace("@@Password", partnerUserDetail.getActualPassword());
			sendMail.sendEmail(partnerUserDetail.getEmailAddress(),emailTemplate.getTemplateSubject(),body,EMAILTEMPLATENAME.WELCOME_EMAIL_TEMPLATE.getEmailTempateName());
			String userRegisteredBody= partnerUserDetail.getFirstName() + ", "+partnerUserDetail.getLastName()+" has registered with Cintap with email: "+partnerUserDetail.getEmailAddress();
			sendMail.sendEmailToSales(userRegisteredBody,"New User Registered");
		}
	}

	@Override
	public void buildForgotPasswordTemplate(EmailTemplate emailTemplate, PartnerUserDetail partnerUserDetail) {
		String body = emailTemplate.getTemplateBody();

		if(body!=null && StringUtils.isEmpty(partnerUserDetail.getFirstName()) && StringUtils.isEmpty(partnerUserDetail.getLastName()) && StringUtils.isEmpty(partnerUserDetail.getEmailAddress())
				&& StringUtils.isEmpty(partnerUserDetail.getPassword()))
		{
			body = body.replace("@@FirstName", partnerUserDetail.getFirstName())
					.replace("@@LastName", partnerUserDetail.getLastName())
					.replace("@@Password", partnerUserDetail.getPassword());
			sendMail.sendEmail(partnerUserDetail.getEmailAddress(),emailTemplate.getTemplateSubject(),body,EMAILTEMPLATENAME.FORGOT_PASS_EMAIL_TEMPLATE.getEmailTempateName());
		}
	}

	@Override
	public void buildPartnerInvitationTemplate(EmailTemplate emailTemplate, PartnerInvitation partnerInvitation) {
		String body = emailTemplate.getTemplateBody();
		if(body!=null && StringUtils.isEmpty(partnerInvitation.getFirstName()) && StringUtils.isEmpty(partnerInvitation.getLastName()) && StringUtils.isEmpty(partnerInvitation.getEmail()))
		{
			body = body.replace("@@FirstName", partnerInvitation.getFirstName())
					.replace("@@LastName", partnerInvitation.getLastName())
					.replace("@@SenderName", partnerInvitation.getSenderName())
					.replace("@@SenderOrganization",partnerInvitation.getOrganizationName());
			sendMail.sendEmail(partnerInvitation.getEmail(),emailTemplate.getTemplateSubject(),body,EMAILTEMPLATENAME.PARTNER_INVITATION.getEmailTempateName());
		}

	}
	
	/**
	 * Sending email to Support team regarding Demo request
	 */

	private static final String EMAIL = "support@cintap.com";
	
	@Override
	public void buildAndSendDemoEmailToSupport(EmailDetails emailDetails) {
		EmailTemplate emailTemplate =  fetchEmailTemplate(EMAILTEMPLATENAME.DEMO_REQUEST_SUPPORT.getEmailTempateName());
		if(null!=emailTemplate){
			String body = emailTemplate.getTemplateBody();
			body = body.replace("@@Name", emailDetails.getName())
					.replace("@@Email", emailDetails.getEmail())
					.replace("@@PhoneNumber", emailDetails.getPhoneNumber())
					.replace("@@Subject", emailDetails.getSubject())
					.replace("@@CustomerMessage", emailDetails.getBody());
			sendMail.sendCustomerEmail(EMAIL,EMAIL,emailTemplate.getTemplateSubject(),body,"WEB_EMAIL");
		}else {
			log.info("Email Template "+ EMAILTEMPLATENAME.DEMO_REQUEST_SUPPORT.getEmailTempateName() +" is misisng in the Database");
		}
	}

	/**
	 * Sending Demo confirmation email to Customer who requested from the Cintap.com
	 */
	@Override
	public void buildAndSendDemoEmailToCustomer(EmailDetails emailDetails) {
		EmailTemplate emailTemplate =  fetchEmailTemplate(EMAILTEMPLATENAME.DEMO_REQUEST_CUSTOMER.getEmailTempateName());
		if(null!=emailTemplate) {
			String body = emailTemplate.getTemplateBody();
			sendMail.sendCustomerEmail(EMAIL,emailDetails.getEmail(),emailTemplate.getTemplateSubject(),body,"WEB_EMAIL");
		}else {
			log.info("Email Template "+ EMAILTEMPLATENAME.DEMO_REQUEST_CUSTOMER.getEmailTempateName() +" is misisng in the Database");
		}
	}
}
