package com.partner.serviceimpl.sso;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.partner.config.TwilioConfigProperties;
import com.partner.entity.BpiConfig;
import com.partner.entity.BpiUserIpAddress;
import com.partner.entity.EmailTemplate;
import com.partner.entity.User;
import com.partner.enums.EMAILTEMPLATENAME;
import com.partner.model.ApiResponse;
import com.partner.repository.BpiUserIPAddressRepository;
import com.partner.repository.UserRepository;
import com.partner.service.EmailTemplateService;
import com.partner.service.sso.TwoFactorAuthService;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerConstants;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private BpiUserIPAddressRepository bpiUserIPAddressRepository;
	
	@Autowired
	private EmailTemplateService emailTemplateService;
	
	@Autowired
	TwilioConfigProperties twilioConfigProperties;
	
	@Override
	public ApiResponse validateSecurityCode(String email, int securityCode,String ipAddress) {
		log.info("TwoFactorAuthServiceImpl - validateSecurityCode");
		ApiResponse apiResonse=null;
		Optional<User> userOpt = userRepository.findByEmailAddressAndSecurityCode(email, securityCode);
		if(userOpt.isPresent()) {
			User user = userOpt.get();
			//Validate 2FA expiry
			if(StringUtils.isNoneEmpty(user.getUpdatedDate())) {
				int expiryMinutes=0;
				BpiConfig bpiConfig = commonUtil.findBpiConfigByKey(PartnerConstants.TWO_FA_EXPRITY_MINUTES_KEY);
				if(bpiConfig!=null && StringUtils.isNoneEmpty(bpiConfig.getConfigValue())) {
					expiryMinutes = Integer.valueOf(bpiConfig.getConfigValue());
				}
				log.info("Expiry Minutes :"+expiryMinutes);
				// Validate Expiry minutes, if success then register IP address
				if(commonUtil.validateExpiryMinutes(user.getUpdatedDate(), expiryMinutes)) {
					log.info("Expiry validated and Success");
					// Register new IP into bpi_user_ip table
					try {
						BpiUserIpAddress bpiUserIpAddress = BpiUserIpAddress.builder()
								.userId(user.getUserId())
								.ipAddress(ipAddress)
								.partnerId(user.getPartnerId())
								.createdDate(PartnerUtility.getCurrentDateTime())
								.build();
						bpiUserIPAddressRepository.save(bpiUserIpAddress);
						apiResonse = ApiResponse.builder().statusCode(ResponseCodes.SUCCESS).statusMessage("Security Code validated successfully").build();
					} catch (DataIntegrityViolationException e) {
						apiResonse = ApiResponse.builder().statusCode(ResponseCodes.SUCCESS).statusMessage("Device registration successful").build();
					}
				}else {
					apiResonse = ApiResponse.builder().statusCode(ResponseCodes.EXPIRED_2FA_SECURITY_CODE).statusMessage("Security Code expired").build();
				}
			}
		}else {
			apiResonse = ApiResponse.builder().statusCode(ResponseCodes.INVALID_2FA_SECURITY_CODE).statusMessage("Invalid Security Code").build();		
		}
		return apiResonse;
	}

	@Override
	public ApiResponse resendSecurityCode(String email,String source) {
		log.info("TwoFactorAuthServiceImpl - resendSecurityCode");
		ApiResponse apiResponse=null;
		Optional<User> optUser = userRepository.findByEmailAddress(email);
		if(optUser.isPresent()) {
			User user = optUser.get();
			final int securityCode = PartnerUtility.generateRandomDigits(6);
			
			String body = null;
			String emailTemplateName = null;
			if("2FA".equalsIgnoreCase(source)) {
				body = commonUtil.build2FASmsBody(email,securityCode,twilioConfigProperties.twoFactorSmsTemplate);
				emailTemplateName = EMAILTEMPLATENAME.TWO_FACTOR_AUTHENTICATION_TEMPLATE.getEmailTempateName();
			}else if("CP".equalsIgnoreCase(source)) {
				body = commonUtil.build2FASmsBody(email,securityCode,twilioConfigProperties.changePasswordSmsTemplate);
				emailTemplateName = EMAILTEMPLATENAME.CHANGE_PASSWORD_TEMPLATE.getEmailTempateName();
			}
			
			log.info("2FA body generated :: "+body);
			if(StringUtils.isNotEmpty(body)) {
				// Send Email
				EmailTemplate emailTemplate = emailTemplateService.fetchEmailTemplate(emailTemplateName);
				commonUtil.send2FAAuthCodeEmail(emailTemplate,email,String.valueOf(securityCode));
				// Send SMS
				apiResponse = commonUtil.handleTwilioSMS(user.getContactNumber(), body);
			}
		}else {
			apiResponse = ApiResponse.builder().statusCode(ResponseCodes.EMAIL_NOT_FOUND).statusMessage("Email is not registered with us") .build();
		}
		return apiResponse;
	}

	@Override
	public ApiResponse getFetchSecurityCode(String email) {
		Optional<User> optUser = userRepository.findByEmailAddress(email);
		if(optUser.isPresent()) {
			User user = optUser.get();
			return ApiResponse.builder().statusCode(ResponseCodes.SUCCESS_CODE).statusMessage(ResponseCodes.SUCCESS).securityCode(user.getSecurityCode()).build();
		}else {
			return ApiResponse.builder().statusCode(ResponseCodes.ERROR_CODE).statusMessage(ResponseCodes.FAIL).securityCode(0).build();
		}
	}

}
