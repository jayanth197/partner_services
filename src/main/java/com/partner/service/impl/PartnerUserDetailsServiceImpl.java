package com.partner.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.partner.config.PartnerConfigProperties;
import com.partner.entity.AuditLog;
import com.partner.entity.EmailTemplate;
import com.partner.entity.PartnerInvitation;
import com.partner.entity.PartnerUserDetail;
import com.partner.entity.User;
import com.partner.enums.ACTIONTYPE;
import com.partner.enums.EMAILTEMPLATENAME;
import com.partner.model.ApiResponse;
import com.partner.repository.PartnerInvitationRepository;
import com.partner.repository.PartnerUserDetailsRepository;
import com.partner.repository.UserRepository;
import com.partner.service.EmailTemplateService;
import com.partner.service.PartnerUserDetailsService;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PartnerUserDetailsServiceImpl implements PartnerUserDetailsService{

	private PartnerUserDetailsRepository partnerUserDetailsRepository;
	private EmailTemplateService emailTemplateService;
	private PartnerConfigProperties partnerConfigProperties;
	private PartnerInvitationRepository partnerInvitationRepository;
	private BCryptPasswordEncoder bcryptEncoder;
	private UserRepository userRepository;
	private AuditLogServiceImpl auditLogServiceImpl;

	public PartnerUserDetailsServiceImpl(PartnerUserDetailsRepository partnerUserDetailsRepository,
			EmailTemplateService emailTemplateService,
			PartnerConfigProperties partnerConfigProperties,
			PartnerInvitationRepository partnerInvitationRepository,
			BCryptPasswordEncoder bcryptEncoder,
			UserRepository userRepository ,
			AuditLogServiceImpl auditLogServiceImpl) {
		this.partnerUserDetailsRepository = partnerUserDetailsRepository;
		this.emailTemplateService = emailTemplateService;
		this.partnerConfigProperties = partnerConfigProperties;
		this.partnerInvitationRepository = partnerInvitationRepository;
		this.bcryptEncoder = bcryptEncoder;
		this.userRepository = userRepository;
		this.auditLogServiceImpl = auditLogServiceImpl;
	}

	@Override
	public String validateUser(String userEmail, String password) {
		Optional<PartnerUserDetail> partnerUserDetails= partnerUserDetailsRepository.validateUserAndGetPartnerISAId(userEmail,password);
		if(partnerUserDetails.isPresent()){
			return	null;//partnerUserDetails.get().getPartnerDetailId().getPartnetISAId();
		}else{
			return "User does not exist";
		}
	}

	// Create New Partner 
	@Override
	public ApiResponse saveUser(PartnerUserDetail partnerUserDetail) {
		log.info("UserServiceImpl :: saveUser - "+partnerUserDetail.toString());
		ApiResponse response=null;
		try {
			if(!isEmailExists(partnerUserDetail.getEmailAddress())) {
				//Generate 8 digits password
				String password = PartnerUtility.generatePassword();
				//Generated password should be more than 6 digits
				if(password.length()>=6)
				{					
					partnerUserDetail.setPassword(password);
					log.info("Password got generated::  "+password+"  for the user "+partnerUserDetail.getEmailAddress());
				}
				partnerUserDetail.setActualPassword(password);
				partnerUserDetail.setPassword(bcryptEncoder.encode(partnerUserDetail.getPassword()));
				/**
				 * This change is required for automation test;  It should not go to PRODUCTION
				 */
				if(partnerUserDetail.getEmailAddress().contains("lavanya") && partnerUserDetail.getEmailAddress().contains("@gmail.com")) {
					partnerUserDetail.setPassword("cintap@123");
					partnerUserDetail.setActualPassword("cintap@123");
				}
		
				//Set user default 
				partnerUserDetail.setAccessLevelId(1);
				partnerUserDetail.setUserTypeId(1);
				partnerUserDetail.setDetailsRequired(true);
				partnerUserDetail.setStatusId(5);
				partnerUserDetail.setCreatedDate(PartnerUtility.getCurrentDateTime());
				partnerUserDetail.setCreatedBy(partnerUserDetail.getCreatedBy());
				PartnerUserDetail ptnrDetailsObject = partnerUserDetailsRepository.save(partnerUserDetail);
				partnerUserDetail.setPartnerUserId(ptnrDetailsObject.getPartnerUserId());
				
				/**
				 * Store into bpi_user table for the first time so that from Welcome template when user tries to login 
				 * Login validation is going to happen from bpi_user table only thats why need to insert record
				 */
				User user = User.builder()
						.contactNumber(partnerUserDetail.getContactNumber())
						.isDetailsRequired(false)
						.emailAddress(partnerUserDetail.getEmailAddress())
						.firstName(partnerUserDetail.getFirstName())
						.lastName(partnerUserDetail.getLastName())
						.middleInitial(partnerUserDetail.getMiddleName())
						.password(partnerUserDetail.getPassword())
						.organization(partnerUserDetail.getOrganizationName())
						.permission("Admin")
						.type("Read-Write")
						.isDetailsRequired(true)
						.partnerUserId(partnerUserDetail.getPartnerUserId())
						.userName(partnerUserDetail.getEmailAddress())
						.createdDate(PartnerUtility.getCurrentDateTime())
						.createdBy(partnerUserDetail.getFirstName()+" "+partnerUserDetail.getLastName())
						.build();
				userRepository.save(user);
				
				//Fetch welcome email template from database
				EmailTemplate emailTemplate = emailTemplateService.fetchEmailTemplate(EMAILTEMPLATENAME.WELCOME_EMAIL_TEMPLATE.getEmailTempateName());
				emailTemplateService.buildWelcomeTemplate(emailTemplate, partnerUserDetail);
				response = new ApiResponse();
				response.setStatusCode(ResponseCodes.SUCCESS);
				response.setStatusMessage("Partner onboarding is completed sucessfully");
				
				//CREATE Audit log - Ankitha
				
				AuditLog auditLog = AuditLog.builder()
						.actionType(ACTIONTYPE.CREATE.getActiontType())
						.action(partnerUserDetail.getAction())
						.createdDate(PartnerUtility.getCurrentDateTime())
						.createdBy(partnerUserDetail.getCreatedBy())
						.partnerId(partnerUserDetail.getPartnerUserId())
						.build();
				
				auditLogServiceImpl.saveAuditLog(auditLog);
				
				log.info("UserServiceImpl :: END");
			}else {
				return buildApiResponse();
			}
		}catch(DataIntegrityViolationException exception) {
			return buildApiResponse();
		}
		return response;
	}

	private ApiResponse buildApiResponse() {
		return ApiResponse.builder().statusCode(ResponseCodes.DUPLICATE_EMAIL_ADDRESS).statusMessage(partnerConfigProperties.emailDuplicateMessage).build();
	}

	private boolean isEmailExists(String emailAddress) {
		boolean isExists=false;
		Optional<PartnerUserDetail> partner = partnerUserDetailsRepository.findByEmailAddress(emailAddress);
		if(partner.isPresent()) {
			isExists = true;
		}
		return isExists;
	}

	@Override
	public List<PartnerUserDetail> findAll() {
		Iterable<PartnerUserDetail> it = partnerUserDetailsRepository.findAll();
		List<PartnerUserDetail> lstUsers = new ArrayList<>();
		it.forEach(e->lstUsers.add(e));
		return lstUsers;
	}

	@Override
	public ApiResponse sendPartnerInvitation(PartnerInvitation partnerInvitation) {
		ApiResponse apiRepsponse=null;
		partnerInvitation.setCreatedDate(PartnerUtility.getCurrentDateTime());
		partnerInvitation.setCreatedBy(partnerInvitation.getCreatedBy());
		PartnerInvitation pInvitation = partnerInvitationRepository.save(partnerInvitation);
		if(pInvitation.getTpInvId()>0) {
			apiRepsponse = new ApiResponse();
			apiRepsponse.setStatusCode(ResponseCodes.SUCCESS);
			apiRepsponse.setStatusMessage(partnerConfigProperties.partnerInvitationSuccess);
			//Send email
			EmailTemplate emailTemplate = emailTemplateService.fetchEmailTemplate(EMAILTEMPLATENAME.PARTNER_INVITATION.getEmailTempateName());
			emailTemplateService.buildPartnerInvitationTemplate(emailTemplate, partnerInvitation);
		}
		
		//Add CREATE Audit log - Ankitha
		AuditLog auditLog = AuditLog.builder()
				.actionType(ACTIONTYPE.CREATE.getActiontType())
				.action(partnerInvitation.getAction())
				.createdDate(PartnerUtility.getCurrentDateTime())
				.createdBy(partnerInvitation.getCreatedBy())
				.partnerId(partnerInvitation.getTpInvId())
				.build();
		
		auditLogServiceImpl.saveAuditLog(auditLog);
		
		return apiRepsponse;
	}
}
