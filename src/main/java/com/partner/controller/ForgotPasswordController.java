package com.partner.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.model.ApiResponse;
import com.partner.service.ForgotPasswordService;
import com.partner.service.sso.TwoFactorAuthService;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1")
public class ForgotPasswordController {

	private TwoFactorAuthService twoFactorAuthService; 
	private ForgotPasswordService changePasswordService; 
	
	public ForgotPasswordController(ForgotPasswordService changePasswordService,
			TwoFactorAuthService twoFactorAuthService) 
	{
		this.changePasswordService = changePasswordService;
		this.twoFactorAuthService = twoFactorAuthService;
	}
	
	
	@PostMapping("/otp/{email}")
	public ResponseEntity<ApiResponse> sendChangePasswordOtp(@PathVariable final String email)
	{
		log.info("TwoFactorAuthController - validateSecurityCode - Request : email "+email);
		ApiResponse apiResponse=null;
		apiResponse = twoFactorAuthService.resendSecurityCode(email,"CP"); // Here CP indicates Change Password
		if(apiResponse!=null) {
			apiResponse.setStatusCode(ResponseCodes.SUCCESS_CODE);
			apiResponse.setStatusMessage(ResponseCodes.SUCCESS);
		}
		log.info("TwoFactorAuthController - validateSecurityCode - Response : "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	
	@PostMapping("/otp/validate/{email}/{password}/{otp}")
	public ResponseEntity<ApiResponse> validateChangePasswordOtp(@PathVariable final String email,@PathVariable final String password,@PathVariable final Integer otp)
	{
		log.info("TwoFactorAuthController - validateChangePasswordOtp - Request : email "+email+" security Code "+otp);
		ApiResponse apiResponse=null;
		apiResponse = changePasswordService.validateOtp(email,password,otp);
		log.info("TwoFactorAuthController - validateSecurityCode - Response : "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

}
