package com.partner.serviceimpl.sso;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.partner.config.PartnerConfigProperties;
import com.partner.config.TwilioConfigProperties;
import com.partner.entity.EmailTemplate;
import com.partner.entity.PartnerUserDetail;
import com.partner.entity.User;
import com.partner.enums.EMAILTEMPLATENAME;
import com.partner.exception.InvalidCredentialsException;
import com.partner.model.ApiResponse;
import com.partner.repository.BpiUserIPAddressRepository;
import com.partner.repository.UserRepository;
import com.partner.service.EmailTemplateService;
import com.partner.service.LoginService;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@Service(value = "loginService")
@Slf4j
public class LoginServiceImpl implements UserDetailsService,LoginService{

	private UserRepository userRepository;
	private CommonUtil commonUtil;
	private PartnerConfigProperties partnerConfigProperties;
	private BpiUserIPAddressRepository bpiUserIPAddressRepository;
	private EmailTemplateService emailTemplateService;
	private TwilioConfigProperties twilioConfigProperties;
	
	public LoginServiceImpl(UserRepository userRepository,CommonUtil commonUtil
			,PartnerConfigProperties partnerConfigProperties,
			BpiUserIPAddressRepository bpiUserIPAddressRepository,
			EmailTemplateService emailTemplateService,
			TwilioConfigProperties twilioConfigProperties) {
		this.userRepository = userRepository;
		this.commonUtil = commonUtil;
		this.partnerConfigProperties = partnerConfigProperties;
		this.bpiUserIPAddressRepository = bpiUserIPAddressRepository;
		this.emailTemplateService = emailTemplateService;
		this.twilioConfigProperties = twilioConfigProperties;
	}

	/**
	 * Procedure: 
	 * Step1: Validate username and password, if valid then verify IP address otherwise sent Invalid Username and password message
	 * Step2: Validate IP Address - If logged in from registered IP address then return User Object otherwise Send 2FA code via Email and SMS
	 * Step3: If 2FA sent successfully then send response with 2FA status code otherwise, user has to retry again. 
	 */
	@Override
	public Object findByUser(PartnerUserDetail partnerUserDetail,String ipAddress,String source) {
		log.info("LoginServiceImpl :: findByUser - "+PartnerUtility.convertObjectToJson(partnerUserDetail));
		User userObj = null;
		// Validating Username and Password
		Optional<User> loginUserOpt=null;

		loginUserOpt = userRepository.findByUserName(partnerUserDetail.getEmailAddress());

		if(loginUserOpt.isPresent()){
			User user = loginUserOpt.get();
			// Validate IP address
			boolean isValid = validateIPAddress(user.getUserId(),ipAddress);
			if(isValid) {
				userObj = new User();
				userObj = user;
			}else{
				log.info("********* Logged in from NEW DEVICE **********");
				final int securityCode = PartnerUtility.generateRandomDigits(6);
				log.info("Secuirty code generated = "+securityCode);
				String body = commonUtil.build2FASmsBody(partnerUserDetail.getEmailAddress(),securityCode,twilioConfigProperties.twoFactorSmsTemplate);
				log.info("2FA body generated :: "+body);
				if(StringUtils.isNotEmpty(body)) {
					// Send Email
					EmailTemplate emailTemplate = emailTemplateService.fetchEmailTemplate(EMAILTEMPLATENAME.TWO_FACTOR_AUTHENTICATION_TEMPLATE.getEmailTempateName());
					commonUtil.send2FAAuthCodeEmail(emailTemplate,partnerUserDetail.getEmailAddress(),String.valueOf(securityCode));
					// Send SMS
					//return commonUtil.handleTwilioSMS(user.getContactNumber(), body);
					return ApiResponse.builder().statusCode("2FA").statusMessage("2FA SENT SUCCESSFULLY").build();
				}
			}
		}else {
			log.info("LoginServiceImpl :: findByUser - Invalid Credentials");
			throw new InvalidCredentialsException(ResponseCodes.INVALID_LOGIN_CREDENTIALS,partnerConfigProperties.loginFailMessage);
		}
		return userObj;
	}

	/**
	 * Added below methods for JWT Token
	 */
	@Override
	public boolean validateIPAddress(int userId,String ipAddress) {
		return bpiUserIPAddressRepository.findByUserIdAndIpAddress(userId, ipAddress).isPresent();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=null;
		Optional<User> loginUserOpt = userRepository.findByUserName(username);
		if(loginUserOpt.isPresent()){
			user = loginUserOpt.get();
		}else {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), getAuthority());
	}

	private List<SimpleGrantedAuthority> getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}
	/**
	 *	End of JWT Token 
	 */
}
