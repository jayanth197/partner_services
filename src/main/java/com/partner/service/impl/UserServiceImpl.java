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
import com.partner.entity.PartnerUserDetail;
import com.partner.entity.User;
import com.partner.enums.ACTIONTYPE;
import com.partner.enums.EMAILTEMPLATENAME;
import com.partner.model.ApiResponse;
import com.partner.model.ChangePassword;
import com.partner.repository.PartnerUserDetailsRepository;
import com.partner.repository.UserRepository;
import com.partner.service.EmailTemplateService;
import com.partner.service.UserService;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{
	
	private BCryptPasswordEncoder bcryptEncoder;
	private UserRepository userRepository;
	private PartnerConfigProperties partnerConfigProperties;
	private EmailTemplateService emailTemplateService; 
	private PartnerUserDetailsRepository partnerUserDetailsRepository;
	private AuditLogServiceImpl auditLogServiceImpl;

	public UserServiceImpl(BCryptPasswordEncoder bcryptEncoder,UserRepository userRepository,
			PartnerConfigProperties partnerConfigProperties,
			EmailTemplateService emailTemplateService,
			PartnerUserDetailsRepository partnerUserDetailsRepository ,
			AuditLogServiceImpl auditLogServiceImpl) {
		this.bcryptEncoder = bcryptEncoder;
		this.userRepository = userRepository;
		this.partnerConfigProperties = partnerConfigProperties;
		this.emailTemplateService = emailTemplateService;
		this.partnerUserDetailsRepository = partnerUserDetailsRepository;
		this.auditLogServiceImpl = auditLogServiceImpl;
	}

	@Override
	public ApiResponse saveUser(User user) {
		log.info("UserServiceImpl :: Begining"+user.toString());
		ApiResponse response=null;
		try {
			user.setUserName(user.getEmailAddress());
			String password = PartnerUtility.generatePassword();

			if(password.length()>=6)
			{
				user.setPassword(password);
				user.setActualPassword(password);
			}else {
				//Setting default passowrd from properties file if password is not generated
				user.setPassword(partnerConfigProperties.defaultPassword);
				user.setActualPassword(partnerConfigProperties.defaultPassword);
			}

			if(user.getUserId()>0) {
				user.setUpdatedDate(PartnerUtility.getCurrentDateTime());
			}else {
				user.setCreatedDate(PartnerUtility.getCurrentDateTime());
			}
			user.setPassword(bcryptEncoder.encode(user.getPassword()));
			user.setStatus(1);
			userRepository.save(user);
			log.info("UserServiceImpl :: User Registered Successfully");
			EmailTemplate emailTemplate = emailTemplateService.fetchEmailTemplate(EMAILTEMPLATENAME.WELCOME_EMAIL_TEMPLATE.getEmailTempateName());
			PartnerUserDetail partnerUserDetail = PartnerUserDetail.builder()
					.firstName(user.getFirstName())
					.lastName(user.getLastName())
					.contactNumber(user.getContactNumber())
					.emailAddress(user.getEmailAddress())
					.organizationName(user.getOrganization())
					.password(user.getPassword())
					.actualPassword(user.getActualPassword())
					.isDetailsRequired(true)
					.accessLevelId(1)
					.userTypeId(1)
					.statusId(5)
					.build();
			emailTemplateService.buildWelcomeTemplate(emailTemplate, partnerUserDetail);
			response = new ApiResponse();
			response.setStatusCode(ResponseCodes.SUCCESS);
			response.setStatusMessage("User onboarding is completed sucessfully");
			
			// Add CREATE audit log - Ankitha
			AuditLog auditLog = AuditLog.builder()
					.actionType(ACTIONTYPE.CREATE.getActiontType())
					.action(user.getAction())
					.createdDate(PartnerUtility.getCurrentDateTime())
					.createdBy(user.getCreatedBy())
					.partnerId(user.getPartnerId())
					.build();
			
			auditLogServiceImpl.saveAuditLog(auditLog);
			
		}catch(DataIntegrityViolationException exception) {
			log.info("Data base exception during save User : {}",exception);
			//throw new CintapBpiException(CintapBpiResponseCodes.DUPLICATE_EMAIL_ADDRESS,getMessage().get(MessageKey.DUPLICATE_EMAIL_KEY));
		}
		log.info("UserServiceImpl : Response - {} ",PartnerUtility.convertObjectToJson(response));
		return response;
	}

	@Override
	public List<User> findAll() {
		Iterable<User> it = userRepository.findAll();
		List<User> lstUsers = new ArrayList<>();
		it.forEach(e->lstUsers.add(e));
		return lstUsers;
	}

	@Override
	public List<User> findAllByPartnerId(int partnerId) {
		List<User> lstUser;
		Optional<List<User>> optListUser= userRepository.findByPartnerIdOrderByUserIdDesc(partnerId);
		if(optListUser.isPresent()) {
			lstUser = optListUser.get();
		}else {
			lstUser = new ArrayList<>();
		}
		return lstUser;
	}

	@Override
	public ApiResponse validateUser(String emailAddress) {
		ApiResponse response=null;
		Optional<PartnerUserDetail> partnerUserDetail = partnerUserDetailsRepository.findByEmailAddress(emailAddress);
		if(partnerUserDetail.isPresent()){
			PartnerUserDetail partnerObject = partnerUserDetail.get();
			EmailTemplate emailTemplate = emailTemplateService.fetchEmailTemplate(EMAILTEMPLATENAME.FORGOT_PASS_EMAIL_TEMPLATE.getEmailTempateName());
			emailTemplateService.buildForgotPasswordTemplate(emailTemplate, partnerObject);
		}else {
			log.info("Invalid Email address"+emailAddress);
			response = new ApiResponse();
			response.setStatusCode(ResponseCodes.INVALID_USER_CODE);
			response.setStatusMessage(partnerConfigProperties.loginFailMessage);
		}
		response = new ApiResponse();
		response.setStatusCode(ResponseCodes.SUCCESS_CODE);
		response.setStatusMessage(ResponseCodes.SUCCESS);
		return response;
	}

	@Override
	public ApiResponse changePassword(ChangePassword changePassword) {
		String newPassword = bcryptEncoder.encode(changePassword.getCurrentPassword());
		int count = userRepository.updatePassword(changePassword.getEmail(), newPassword);
		//int count = getPartnerUserDetailsRepository().updatePassword(changePassword.getCurrentPassword(),changePassword.getPreviousPassword(),changePassword.getEmail());
		ApiResponse response = new ApiResponse();

		if(count>0) {
			response.setStatusCode(ResponseCodes.SUCCESS);
			response.setStatusMessage(partnerConfigProperties.passwordChangeSuccess);
		}else {
			response.setStatusCode(ResponseCodes.ERROR_CODE);
			response.setStatusMessage(partnerConfigProperties.invalidUserName);
		}

		// Password Change audit log - Ankitha
		AuditLog auditLog = AuditLog.builder()
				.actionType(ACTIONTYPE.PASSWORD_CHANGE.getActiontType())
				.action(changePassword.getAction())
				.createdDate(PartnerUtility.getCurrentDateTime())
				.createdBy(changePassword.getCreatedBy())
				.partnerId(changePassword.getPartnerId())
				.build();
		
		auditLogServiceImpl.saveAuditLog(auditLog);
		
		return response;
	}

}
