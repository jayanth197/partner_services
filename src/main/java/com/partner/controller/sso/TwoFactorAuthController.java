package com.partner.controller.sso;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.model.ApiResponse;
import com.partner.service.sso.TwoFactorAuthService;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerUtility;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
public class TwoFactorAuthController{

	@Autowired
	private TwoFactorAuthService twoFactorAuthService;
	@Autowired
	private CommonUtil commonUtil;
	

	@PostMapping("/2fa/{email}/{securityCode}")
	public ResponseEntity<ApiResponse> validateSecurityCode(HttpServletRequest request,@PathVariable String email,@PathVariable Integer securityCode)
	{
		log.info("TwoFactorAuthController - validateSecurityCode - Request : email "+email+" security Code "+securityCode);
		String ipAddress = commonUtil.fetchIPAddress(request);
		ApiResponse apiResponse=null;
		apiResponse = twoFactorAuthService.validateSecurityCode(email, securityCode,ipAddress);
		log.info("TwoFactorAuthController - validateSecurityCode - Response : "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	@PostMapping("/2fa/resend/{email}")
	public ResponseEntity<ApiResponse> resendSecurityCode(@PathVariable String email)
	{
		log.info("TwoFactorAuthController - validateSecurityCode - Request : email "+email);
		ApiResponse apiResponse=null;
		apiResponse = twoFactorAuthService.resendSecurityCode(email,"CP");
		log.info("TwoFactorAuthController - validateSecurityCode - Response : "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}
	
	
	@GetMapping("/2fa/otp/{email}")
	public ResponseEntity<ApiResponse> getSecurityCode(@PathVariable String email)
	{
		log.info("TwoFactorAuthController - validateSecurityCode - Request : email "+email);
		ApiResponse apiResponse=null;
		apiResponse = twoFactorAuthService.getFetchSecurityCode(email);
		log.info("TwoFactorAuthController - getSecurityCode - Response : "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}
}
