package com.partner.service;

import com.partner.entity.EmailTemplate;
import com.partner.entity.PartnerInvitation;
import com.partner.entity.PartnerUserDetail;
import com.partner.model.EmailDetails;

public interface EmailTemplateService {
	 EmailTemplate fetchEmailTemplate(String name);
	 void buildWelcomeTemplate(EmailTemplate emailTemplate, PartnerUserDetail partnerUserDetail);
	 void buildForgotPasswordTemplate(EmailTemplate emailTemplate, PartnerUserDetail partnerUserDetail);
	 void buildPartnerInvitationTemplate(EmailTemplate emailTemplate, PartnerInvitation partnerInvitation);
	 void buildAndSendDemoEmailToSupport(EmailDetails emailDetails);
	 void buildAndSendDemoEmailToCustomer(EmailDetails emailDetails);
}
