package com.partner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.model.ApiResponse;
import com.partner.model.EmailDetails;
import com.partner.service.EmailTemplateService;
import com.partner.service.SendMail;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1")
public class WebsiteEmailController {

	@Autowired
	SendMail sendMail;

	@Autowired
	EmailTemplateService emailTemplateService;

	@PostMapping(path="/web/sendmail")
	public ResponseEntity<ApiResponse> sendCustomerEmail(@RequestBody EmailDetails emailDetails)
	{
		log.info("WebsiteEmailController | sendCustomerEmail - Request : "+PartnerUtility.convertObjectToJson(emailDetails));
		ApiResponse apiResponse = null;
		emailTemplateService.buildAndSendDemoEmailToSupport(emailDetails);
		log.info("Email sent to Support");
		emailTemplateService.buildAndSendDemoEmailToCustomer(emailDetails);
		log.info("Email sent to Customer");
		apiResponse = ApiResponse.builder().statusCode(ResponseCodes.SUCCESS_CODE).statusMessage(ResponseCodes.SUCCESS).build();
		log.info("WebsiteEmailController | sendCustomerEmail - Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}
	
}
