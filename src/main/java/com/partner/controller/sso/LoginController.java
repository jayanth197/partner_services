package com.partner.controller.sso;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.config.PartnerConfigProperties;
import com.partner.entity.PartnerUserAssociation;
import com.partner.entity.PartnerUserDetail;
import com.partner.model.ApiResponse;
import com.partner.repository.PartnerUserAssociationRepository;
import com.partner.service.LoginService;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/v1")
@Slf4j
public class LoginController{

	private CommonUtil commonUtil;
	private LoginService loginService;
	private PartnerConfigProperties partnerConfigProperties;
	private PartnerUserAssociationRepository partnerUserAssociationRepository;

	public LoginController(CommonUtil commonUtil,LoginService loginService,
			PartnerConfigProperties partnerConfigProperties,
			PartnerUserAssociationRepository partnerUserAssociationRepository) {
		this.commonUtil = commonUtil;
		this.loginService = loginService;
		this.partnerConfigProperties = partnerConfigProperties;
		this.partnerUserAssociationRepository = partnerUserAssociationRepository;
	}


	@PostMapping(value="/login",produces="application/json",consumes="application/json")
	public ResponseEntity<Object> login(HttpServletRequest request, @RequestBody PartnerUserDetail partnerUserDetail)
	{
		log.info("LoginController : login - Request : "+PartnerUtility.convertObjectToJson(partnerUserDetail));
		String ipAddress = commonUtil.fetchIPAddress(request);
		log.info("LoginController : login - Request received from IP address : "+ipAddress);
		Object userObj=null;
		ApiResponse response = null;
		userObj = loginService.findByUser(partnerUserDetail,ipAddress,"LOGIN");
		if(userObj==null) {
			log.info("LoginController :: login - Record not found");
			response = new ApiResponse();
			response.setStatusCode(ResponseCodes.INVALID_LOGIN_CREDENTIALS);
			response.setStatusMessage(partnerConfigProperties.loginFailMessage);
			return new ResponseEntity<>(response,HttpStatus.OK);
		}
		log.info("LoginController | login : Response - "+PartnerUtility.convertObjectToJson(userObj));
		return new ResponseEntity<>(userObj,HttpStatus.OK);
	}

	@GetMapping(value="/getpartnerid/{userId}")
	public ResponseEntity<PartnerUserAssociation> getPartnerByUserId(@PathVariable("userId") final int userId)
	{
		log.info("LoginController | getPartnerByUserId - Request : "+PartnerUtility.convertObjectToJson(userId));
		PartnerUserAssociation partnerAssociation=null;
		Optional<PartnerUserAssociation> partnerUserAssociation = partnerUserAssociationRepository.findByUserId(userId);
		if(partnerUserAssociation.isPresent()) {
			partnerAssociation = partnerUserAssociation.get();
		}
		log.info("LoginController | getPartnerByUserId : Response - "+PartnerUtility.convertObjectToJson(partnerAssociation));
		return new ResponseEntity<>(partnerAssociation,HttpStatus.OK);
	}
}
