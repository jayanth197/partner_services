package com.partner.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.entity.PartnerInvitation;
import com.partner.entity.PartnerUserDetail;
import com.partner.model.ApiResponse;
import com.partner.model.PartnerSearchCriteria;
import com.partner.model.PartnerSearchResponse;
import com.partner.repository.PartnerInvitationRepository;
import com.partner.repository.PartnerUserDetailsRepository;
import com.partner.service.PartnerUserDetailsService;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerUtility;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/v1")
@Slf4j
public class PartnerUserDetailsController{

	private PartnerUserDetailsService partnerUserDetailsService;
	private PartnerInvitationRepository partnerInvitationRepository;
	private CommonUtil commonUtil;
	private PartnerUserDetailsRepository partnerUserDetailsRepository; 
	
	public PartnerUserDetailsController(PartnerUserDetailsService partnerUserDetailsService,
			PartnerInvitationRepository partnerInvitationRepository,
			CommonUtil commonUtil,
			PartnerUserDetailsRepository partnerUserDetailsRepository) {
		this.partnerUserDetailsService = partnerUserDetailsService;
		this.partnerInvitationRepository = partnerInvitationRepository;
		this.commonUtil = commonUtil;
		this.partnerUserDetailsRepository = partnerUserDetailsRepository;
	}


	@PostMapping(value="/user")
	public ResponseEntity<ApiResponse> saveUser(HttpServletRequest request,@Valid @RequestBody PartnerUserDetail partnerUserDetail)
	{
		log.info("PartnerUserDetailsController : saveUser - Request : "+PartnerUtility.convertObjectToJson(partnerUserDetail));
		ApiResponse apiResponse = null;
		apiResponse = partnerUserDetailsService.saveUser(partnerUserDetail);
		log.info("PartnerUserDetailsController | saveUser : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	@GetMapping(value="/users")
	public ResponseEntity<List<PartnerUserDetail>> getAllUsers()
	{
		log.info("PartnerDetailsController | getAllUsers : Request received");
		List<PartnerUserDetail> lstUsers=null;
		lstUsers = partnerUserDetailsService.findAll();
		log.info("PartnerUserDetailsController :: getAllUsers - ",lstUsers.size());

		log.info("PartnerUserDetailsController | getAllUsers : Response - "+lstUsers.size());
		return new ResponseEntity<>(lstUsers,HttpStatus.OK);
	}

	@PostMapping(value="/sendpartnerinvitation")
	public ResponseEntity<ApiResponse> sendPartnerInvitation(@Valid @RequestBody PartnerInvitation partnerInvitation)
	{
		log.info("PartnerUserDetailsController | sendPartnerInvitation - Request : "+PartnerUtility.convertObjectToJson(partnerInvitation));
		ApiResponse apiResponse = null;
		apiResponse = partnerUserDetailsService.sendPartnerInvitation(partnerInvitation);
		log.info("PartnerUserDetailsController | sendPartnerInvitation : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	//Partner Invited details
	@GetMapping(value="/invitetp/{partnerId}")
	public ResponseEntity<List<PartnerInvitation>> getAllInvitedUsers(@PathVariable("partnerId") String partnerId)
	{
		log.info("PartnerUserDetailsController | getAllInvitedUsers : Request : partnerId - "+partnerId);
		List<PartnerInvitation> lstInvitedPartners=null;
		Optional<List<PartnerInvitation>> listPartnerInvitations = partnerInvitationRepository.findBySenderPartnerId(partnerId);
		if(listPartnerInvitations.isPresent()) {
			lstInvitedPartners = listPartnerInvitations.get();
			log.info("PartnerUserDetailsController :: getAllInvitedUsers - ",lstInvitedPartners.size());
		}
		log.info("PartnerUserDetailsController | getAllInvitedUsers : Response - "+lstInvitedPartners.size());
		return new ResponseEntity<>(lstInvitedPartners,HttpStatus.OK);
	}

	@GetMapping(value="/ptnruserdtl/{emailId}") 
	public ResponseEntity<PartnerUserDetail> getUserByEmailId(@PathVariable("emailId") String emailId){ 
		log.info("PartnerUserDetail | getUserByEmailId : Request : emailId - "+emailId);
		PartnerUserDetail partnerUserDetail=null; 
		Optional<PartnerUserDetail> optPartnerUserDetail = partnerUserDetailsRepository.findByEmailAddress(emailId); 
		if(optPartnerUserDetail.isPresent()) {
			partnerUserDetail = optPartnerUserDetail.get();
		}
		log.info("PartnerUserDetailsController | getUserByEmailId : Response - "+PartnerUtility.convertObjectToJson(partnerUserDetail));
		return new ResponseEntity<>(partnerUserDetail,HttpStatus.OK); 
	}

	/** 
	 * Partner Details with Pagination
	 *
	 */
	@PostMapping(value="/invitetp")
	public ResponseEntity<PartnerSearchResponse> fetchAllInvitedUsers(@RequestBody PartnerSearchCriteria ptnrSearchCriteria)
	{
		log.info("PartnerUserDetailsController | fetchAllInvitedUsers : Request : - "+PartnerUtility.convertObjectToJson(ptnrSearchCriteria));
		PartnerSearchResponse ptnrSearchResponse=null;

		Pageable paging = PageRequest.of(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize());

		Page<PartnerInvitation> pagePartnerInvitation = partnerInvitationRepository.findBySenderPartnerId(ptnrSearchCriteria.getPartnerId(), paging);

		if(pagePartnerInvitation.hasContent()) {
			int totalPages = pagePartnerInvitation.getTotalPages();
			List<PartnerInvitation> lstPartnerInvitation = pagePartnerInvitation.getContent();
			ptnrSearchResponse = PartnerSearchResponse.builder()
					.pageble(commonUtil.buildPage(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize(),totalPages))
					.partnerInvitation(lstPartnerInvitation)
					.build();  
		}
		log.info("PartnerUserDetailsController | fetchAllInvitedUsers :Response - "+ptnrSearchResponse.getPartnerInvitation().size());
		return new ResponseEntity<>(ptnrSearchResponse,HttpStatus.OK);
	}

}
