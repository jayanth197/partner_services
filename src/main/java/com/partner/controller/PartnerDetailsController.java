package com.partner.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.config.PartnerConfigProperties;
import com.partner.entity.PartnerAssociation;
import com.partner.entity.PartnerDetails;
import com.partner.enums.INVITATIONSTATUS;
import com.partner.exception.PartnerNotFoundException;
import com.partner.model.ApiResponse;
import com.partner.model.PartnerSearchCriteria;
import com.partner.model.PartnerSearchResponse;
import com.partner.repository.PartnerDetailsRepository;
import com.partner.service.PartnerAssociationService;
import com.partner.service.PartnerDetailsService;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value="/v1")
@Slf4j
public class PartnerDetailsController {

	private CommonUtil commonUtil;
	private PartnerDetailsService partnerDetailsService;
	private PartnerDetailsRepository partnerDetailsRepository;
	private PartnerAssociationService partnerAssociationService;
	@Autowired
	PartnerConfigProperties partnerConfigProperties;
	
	public PartnerDetailsController(CommonUtil commonUtil,
			PartnerDetailsService partnerDetailsService,
			PartnerDetailsRepository partnerDetailsRepository,
			PartnerAssociationService partnerAssociationService) {
		this.commonUtil = commonUtil;
		this.partnerDetailsService = partnerDetailsService;
		this.partnerDetailsRepository = partnerDetailsRepository;
		this.partnerAssociationService = partnerAssociationService;
	}
	
	@PostMapping("/partner")
	public ResponseEntity<ApiResponse> savePartnerDetails(HttpServletRequest request, @RequestBody PartnerDetails partnerDetails)
	{
		log.info("PartnerDetailsController | savePartnerDetails - Request : "+PartnerUtility.convertObjectToJson(partnerDetails));
		ApiResponse apiResponse = null;
		partnerDetails.setIpAddress(commonUtil.fetchIPAddress(request));
		apiResponse = partnerDetailsService.savePartnerDetails(partnerDetails);
		log.info("PartnerDetailsController | savePartnerDetails : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	/**Ankitha : Need to remove this method because pagination method implemented below - fetchAllPartners()*/
	@GetMapping(value="/partner")
	public ResponseEntity<List<PartnerDetails>> getPartnerDetails()
	{
		log.info("PartnerDetailsController | getPartnerDetails : Request received");
		List<PartnerDetails> lstPartnerDetails = null;
		//call below method to get all partner details including logged in partner also - This method is suitable for Admin
		lstPartnerDetails = partnerDetailsService.getAllPartners();

		log.info("lstPartnerDetails | getPartnerDetails : Response - "+lstPartnerDetails.size());
		return new ResponseEntity<>(lstPartnerDetails,HttpStatus.OK);
	}

	//Fetch all partner details except logged in partner
	@GetMapping(value="/partner/{partnerId}")
	public ResponseEntity<List<PartnerDetails>> getPartnerDetailsExceptPid(@PathVariable("partnerId") final String partnerId)
	{
		log.info("PartnerDetailsController | getPartnerDetailsExceptPid : Request : partnerId - "+partnerId);
		List<PartnerDetails> lstPartnerDetails = null;
		lstPartnerDetails = partnerDetailsService.getAllPartnersExceptPid(partnerId);

		log.info("lstPartnerDetails | getPartnerDetailsExceptPid : Response - "+lstPartnerDetails.size());
		return new ResponseEntity<>(lstPartnerDetails,HttpStatus.OK);
	}

	//Fetching Partner associated partners 
	/*@GetMapping(value="/partner/{pid}")
	public ResponseEntity<List<PartnerDetails>> getPartnerDetailsById(@PathVariable("pid") final int partnerId){
		log.info("PartnerDetailsController :: getPartnerDetailsById - "+partnerId);
		List<PartnerDetails> listMyRequest=null;
		try {
			Optional<List<PartnerDetails>> listParnerAssocation = partnerDetailRepository.findPartnerAssociation(partnerId, CintapBpiStatus.ACTIVE.getStatusId());
			if(listParnerAssocation.isPresent()) {
				listMyRequest = listParnerAssocation.get();
				log.info("PartnerDetailsController :: getPartnerDetailsById - "+listMyRequest.size());
			}
		}catch (Exception e) {
			log.info("PartnerDetailsController :: getPartnerDetailsById - "+e.getMessage());
			throw new CintapBpiException(CintapBpiResponseCodes.INTERNAL_TECHNICAL_ERROR,getMessage().get(MessageKey.INTERNAL_TECHNICAL_ERROR_KEY));
		}
		return new ResponseEntity<>(listMyRequest,HttpStatus.OK);
	}*/

	//Fetching My Sent Partner invitations 
	@GetMapping(value="/partnersentinvitation/{pid}")
	public ResponseEntity<List<PartnerDetails>> getSentPartnerInvitations(@PathVariable("pid") final int partnerId){
		log.info("PartnerDetailsController | getSentPartnerInvitations : Request : partnerId - "+partnerId);
		List<PartnerDetails> listMyRequest=null;

		Optional<List<PartnerDetails>> listParnerAssocation = partnerDetailsRepository.findMyPartnerInvitations(partnerId, INVITATIONSTATUS.INVITED.getStatusId());
		if(listParnerAssocation.isPresent()) {
			listMyRequest = listParnerAssocation.get();
		}

		log.info("lstPartnerDetails | getSentPartnerInvitations : Response - "+listMyRequest.size());
		return new ResponseEntity<>(listMyRequest,HttpStatus.OK);
	}

	//Fetch My Received Partner invitations	 
	@GetMapping(value="/partnerreceivedinvitation/{pid}")
	public ResponseEntity<List<PartnerDetails>> getMyReceivedPartnerInvitations(@PathVariable("pid") final int partnerId){
		log.info("PartnerDetailsController | getMyReceivedPartnerInvitations : Request : partnerId - "+partnerId);
		List<PartnerDetails> listMyRequest=null;

		Optional<List<PartnerDetails>> listParnerAssocation = partnerDetailsRepository.findReceivedPartnerInvitations(partnerId, INVITATIONSTATUS.INVITED.getStatusId());
		if(listParnerAssocation.isPresent()) {
			listMyRequest = listParnerAssocation.get();
			log.info("PartnerDetailsController :: getSentPartnerInvitations - ",listMyRequest.size());
		}
		log.info("lstPartnerDetails | getMyReceivedPartnerInvitations : Response - "+listMyRequest.size());
		return new ResponseEntity<>(listMyRequest,HttpStatus.OK);
	}

	@PutMapping(value="/partner")
	public ResponseEntity<ApiResponse> updatePartnerDetails(@RequestBody PartnerDetails partnerDetails)
	{
		log.info("PartnerDetailsController | updatePartnerDetails - Request : "+PartnerUtility.convertObjectToJson(partnerDetails));
		ApiResponse apiResponse = null;

		partnerDetails.setUpdatedDate(PartnerUtility.getCurrentDateTime());
		apiResponse = partnerDetailsService.updatePartnerDetails(partnerDetails);

		log.info("lstPartnerDetails  | updatePartnerDetails : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	/**
	 * Partner Free text search on partnerid firstname, lastname, middlename, email and phone
	 * @param keyword
	 * @return
	 */
	@GetMapping(value="/searchpartner/{keyword}")
	public ResponseEntity<List<PartnerDetails>> searchPartnerDetails(@PathVariable("keyword") final String keyword)
	{
		log.info("PartnerDetailsController | searchPartnerDetails - Request : "+keyword);
		List<PartnerDetails> listPartnerDetails=null;

		//listPartnerDetails = partnerDetailsService.getFilterPartnerDetails(partnerDetails);
		listPartnerDetails = partnerDetailsService.partnerFreeTextSearch(keyword);
		
		
		
		log.info("lstPartnerDetails | searchPartnerDetails : Response - "+listPartnerDetails.size());
		return new ResponseEntity<>(listPartnerDetails,HttpStatus.OK);
	}
	
	//search partner with pagination
	
	@PostMapping(value="/keyword")
	public ResponseEntity<PartnerSearchResponse> fetchSearchPartnerDetails(@RequestBody PartnerSearchCriteria ptnrSearchCriteria)
	{
		log.info("PartnerDetailsController | fetchSearchPartnerDetails : Request : - "+PartnerUtility.convertObjectToJson(ptnrSearchCriteria));
		PartnerSearchResponse ptnrSearchResponse=null;

		Pageable paging = PageRequest.of(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize());

		Page<PartnerDetails> pagePartnerDetails = partnerDetailsRepository.findByKeyword(ptnrSearchCriteria.getKeyword(), ptnrSearchCriteria.getPartnerId(), paging);

		if(pagePartnerDetails.hasContent()) {
			int totalPages = pagePartnerDetails.getTotalPages();
			List<PartnerDetails> lstPartnerDetails = pagePartnerDetails.getContent();
			ptnrSearchResponse = PartnerSearchResponse.builder()
					.pageble(commonUtil.buildPage(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize(),totalPages))
					.partnerDetails(excludeSelfPartner(lstPartnerDetails,Integer.parseInt(ptnrSearchCriteria.getPartnerId())))
					.build();  
		}else {
			throw new PartnerNotFoundException(ResponseCodes.SEARCH_PARTNER_FAIL_CODE,partnerConfigProperties.searchPartnerFail);
		}
		log.info("PartnerDetailsController | fetchSearchPartnerDetails : Got Response ");
		return new ResponseEntity<>(ptnrSearchResponse,HttpStatus.OK);
	}

	/** 
	 * Exclude self partner details from the list
	 * @param lstPartnerDetails
	 * @param partnerId
	 * @return
	 */
	private List<PartnerDetails> excludeSelfPartner(List<PartnerDetails> lstPartnerDetails,Integer partnerId){
		return lstPartnerDetails
				.stream()
				.filter(producer -> !producer.getPartnerId().equals(partnerId))
				.collect(Collectors.toList());
	}

	@PostMapping(value="/pstatus")
	public ResponseEntity<ApiResponse> activeInactivePartner(@RequestBody PartnerAssociation partnerAssociation)
	{
		log.info("PartnerDetailsController | activeInactivePartner - Request : "+PartnerUtility.convertObjectToJson(partnerAssociation));
		ApiResponse apiResponse = null;
		apiResponse = partnerAssociationService.updateStatus(partnerAssociation);
		log.info("lstPartnerDetails | activeInactivePartner : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	@PostMapping(value="/fetchallpartners/")
	public ResponseEntity<PartnerSearchResponse> fetchAllPartners(@RequestBody PartnerSearchCriteria ptnrSearchCriteria)
	{
		log.info("PartnerDetailsController | fetchAllPartners - Request : partnerId - "+PartnerUtility.convertObjectToJson(ptnrSearchCriteria));
		PartnerSearchResponse ptnrSearchResponse = partnerDetailsService.fetchAllPartners(ptnrSearchCriteria);
		return new ResponseEntity<>(ptnrSearchResponse,HttpStatus.OK);
	}
	
	@GetMapping(value="/fetchpartnerdetails/{partnerId}")
	public ResponseEntity<PartnerDetails> fetchDetailsByPtrnId(@PathVariable final Integer partnerId)
	{
		log.info("PartnerDetailsController | fetchAllPartners - Request : partnerId - "+partnerId);
		PartnerDetails partnerDetails = partnerDetailsService.findByPartnerId(partnerId);
		return new ResponseEntity<>(partnerDetails,HttpStatus.OK);
	}
	
}


