package com.partner.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.partner.entity.PartnerAssociation;
import com.partner.entity.PartnerDetails;
import com.partner.entity.PartnerOrgDetails;
import com.partner.entity.TpExternal;
import com.partner.enums.INVITATIONSTATUS;
import com.partner.enums.PARTNERTYPE;
import com.partner.model.ApiResponse;
import com.partner.model.PartnerSearchCriteria;
import com.partner.model.PartnerSearchResponse;
import com.partner.model.TradingAndExternalPartners;
import com.partner.repository.PartnerAssociationRepository;
import com.partner.repository.PartnerDetailsRepository;
import com.partner.repository.PartnerOrgDetailsRepository;
import com.partner.repository.TpExternalRepository;
import com.partner.service.PartnerAssociationService;
import com.partner.service.PartnerDetailsService;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerUtility;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value="/v1")
@Slf4j
public class PartnerAssociationController {

	@Autowired
	private PartnerAssociationService partnerAssociationService;
	
	@Autowired
	private PartnerAssociationRepository partnerAssociationRepository;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private PartnerDetailsRepository partnerDetailsRepository;
	
	@Autowired
	private PartnerDetailsService partnerDetailsService;
	
	@Autowired
	private TpExternalRepository tpExternalRepository;
	
	@Autowired
	private PartnerOrgDetailsRepository partnerOrgDetailsRepository;

	@PostMapping(value="/passociation")
	public ResponseEntity<ApiResponse> savePartnerAssociation(@RequestBody PartnerAssociation partnerAssociation){
		log.info("PartnerAssociationController : savePartnerAssociation - Request : "+PartnerUtility.convertObjectToJson(partnerAssociation));
		ApiResponse apiResponse = null;
		apiResponse = partnerAssociationService.savePartner(partnerAssociation);
		log.info("ExternalTransactionController | savePartnerAssociation : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	@PostMapping(value="/prequest/{partnerId}")
	public ResponseEntity<List<PartnerDetails>> getMyPartnerRequests(@PathVariable("partnerId") int partnerId){
		log.info("PartnerAssociationController | getMyPartnerRequests - Request : partnerId - "+partnerId);
		List<PartnerDetails> listMyRequest=null;
		Optional<List<PartnerDetails>> listParnerAssocation = partnerDetailsRepository.findPartnerAssociation(partnerId, INVITATIONSTATUS.INVITED.getStatusId());
		if(listParnerAssocation.isPresent()) {
			listMyRequest = listParnerAssocation.get();
			log.info("PartnerAssociationController :: getMyPartnerRequests - ",listMyRequest.size());
		}
		log.info("PartnerAssociationController | getMyPartnerRequests : Response - "+listMyRequest.size());
		return new ResponseEntity<>(listMyRequest,HttpStatus.OK);
	}

	@PutMapping(value="/passociation")
	public ResponseEntity<ApiResponse> updatePartnerInvitationStatus(@RequestBody PartnerAssociation partnerAssociation){
		log.info("PartnerAssociationController | updatePartnerInvitationStatus - Request : "+PartnerUtility.convertObjectToJson(partnerAssociation));
		ApiResponse apiResponse = null;
		partnerAssociation.setUpdatedDate(PartnerUtility.getCurrentDateTime());
		apiResponse = partnerAssociationService.updateInvitationStatus(partnerAssociation);
		log.info("PartnerAssociationController | updatePartnerInvitationStatus : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	@PostMapping(value="/passociationstatus")
	public ResponseEntity<PartnerAssociation> getPartnerAssociationStatus(@RequestBody PartnerAssociation partnerAssociation){
		log.info("PartnerAssociationController | getPartnerAssociationStatus - Request : "+PartnerUtility.convertObjectToJson(partnerAssociation));
		PartnerAssociation partnerAssociationObj = null;
		partnerAssociationObj = partnerAssociationService.getPartnerAssociationStatus(partnerAssociation);
		log.info("PartnerAssociationController | getPartnerAssociationStatus : Response - "+PartnerUtility.convertObjectToJson(partnerAssociationObj));
		return new ResponseEntity<>(partnerAssociationObj,HttpStatus.OK);
	}

	@PostMapping(value="/myassociatedpartners/{partnerId}")
	public ResponseEntity<List<PartnerDetails>> getMyAssociatedPartners(@PathVariable("partnerId") int partnerId){
		log.info("PartnerAssociationController | getMyAssociatedPartners - Request : partnerId - "+partnerId);
		List<PartnerDetails> lstPartnerDetails=null;
		Optional<List<PartnerAssociation>> listPartnerAssociation = partnerAssociationRepository.findPartnerByPartnerId(partnerId, INVITATIONSTATUS.ACTIVE.getStatusId());
		if(listPartnerAssociation.isPresent()) {
			List<PartnerAssociation> lstPartnerAssociation = listPartnerAssociation.get();
			lstPartnerDetails = partnerDetailsService.getPartnerByPartnerIdAndInviteePid(lstPartnerAssociation,partnerId);
		}

		log.info("PartnerAssociationController | getMyAssociatedPartners : Response - "+lstPartnerDetails.size());
		return new ResponseEntity<>(lstPartnerDetails,HttpStatus.OK);
	}

	@PostMapping(value="/myassociatedtpandetp/{partnerId}")
	public ResponseEntity<List<TradingAndExternalPartners>> getMyAssociatedPartnersAndExternalPartners(@PathVariable("partnerId") int partnerId){
		log.info("PartnerAssociationController | getMyAssociatedPartnersAndExternalPartners - Request : partnerId - "+partnerId );
		List<TradingAndExternalPartners> lstTradingExternalPartners= new ArrayList<>();
		//Fetch Associated Partners
		List<PartnerDetails> lstPartnerDetails=null;
		Optional<List<PartnerAssociation>> listPartnerAssociation = partnerAssociationRepository.findPartnerByPartnerId(partnerId, INVITATIONSTATUS.ACTIVE.getStatusId());
		if(listPartnerAssociation.isPresent()) {
			List<PartnerAssociation> lstPartnerAssociation = listPartnerAssociation.get();
			lstPartnerDetails = partnerDetailsService.getPartnerByPartnerIdAndInviteePid(lstPartnerAssociation,partnerId);
		}

		//Fetch Associated External Partners
		List<TpExternal> listTpExternal=new ArrayList<>();
		Optional<List<TpExternal>> optListTpExternal = tpExternalRepository.findByOwnerPartnerID(partnerId);
		if(optListTpExternal.isPresent()) {
			listTpExternal = optListTpExternal.get();
		}
		//Combine Both Partners and External Partners
		if(lstPartnerDetails!=null && !lstPartnerDetails.isEmpty()) {
			for(PartnerDetails pDetails:lstPartnerDetails) {
				TradingAndExternalPartners tpExternal = TradingAndExternalPartners.builder().type(PARTNERTYPE.TRADING_PARTNER.getValue())
						.partner(pDetails.getPartnerId())
						.name(pDetails.getPartnerOrgDetails().getCompanyName())
						.senderIsaId(getPartnerISAId(pDetails.getPartnerId()))
						.userName(pDetails.getFirstName()+" "+pDetails.getLastName())
						.userId(pDetails.getCreatedBy())
						.build();
				lstTradingExternalPartners.add(tpExternal);
			}
		}
		if(listTpExternal!=null && !listTpExternal.isEmpty()) {
			for(TpExternal tpExternal:listTpExternal) {
				TradingAndExternalPartners tpExternalObj = TradingAndExternalPartners.builder()
						.type(PARTNERTYPE.EXTERNAL_PARTNER.getValue())
						.partner(tpExternal.getId())
						.name(tpExternal.getName())
						.senderIsaId(getExternalPartnerISAId(tpExternal.getId()))
						.userName(tpExternal.getName())
						.userId(tpExternal.getCreatedBy())
						.build();
				lstTradingExternalPartners.add(tpExternalObj);
			}
		}

		//Add Application Partner details
		/*List<TpApplication> listTpApplication=new ArrayList<>();
		Optional<List<TpApplication>> optListTpApplication = getTpApplicationRepository().findByOwnerPartnerID(partnerId);
		if(optListTpApplication.isPresent()) {
			listTpApplication = optListTpApplication.get();
		}

		if(listTpApplication!=null && !listTpApplication.isEmpty()) {
			for(TpApplication tpApplication:listTpApplication) {
				TradingAndExternalPartners tpExternalObj = TradingAndExternalPartners.builder()
						.type(PARTNERTYPE.TRADING_APPLICATION.getValue())
						.partner(tpApplication.getId())
						.name(tpApplication.getName())
						.build();
				lstTradingExternalPartners.add(tpExternalObj);
			}
		}*/

		//Adding self partner id details
		Optional<PartnerOrgDetails> optPartnerOrgDetails = partnerOrgDetailsRepository.findByPartnerId(partnerId);
		if(optPartnerOrgDetails.isPresent()) {
			PartnerOrgDetails partnerOrgDetails = optPartnerOrgDetails.get();
			TradingAndExternalPartners tpSelfPartnerObj = TradingAndExternalPartners.builder()
					.type(PARTNERTYPE.TRADING_PARTNER.getValue())
					.partner(partnerOrgDetails.getPartnerDetails().getPartnerId())
					.name(partnerOrgDetails.getCompanyName())
					.senderIsaId(partnerOrgDetails.getEdiIsaId())
					.userName(partnerOrgDetails.getPartnerDetails().getFirstName() + "" + partnerOrgDetails.getPartnerDetails().getLastName())
					.build();
			lstTradingExternalPartners.add(tpSelfPartnerObj);
		}

		log.info("PartnerAssociationController | getMyAssociatedPartnersAndExternalPartners : Response - "+lstTradingExternalPartners.size());
		return new ResponseEntity<>(lstTradingExternalPartners,HttpStatus.OK);
	}

	//My received partner requests
	@PostMapping(value="/partnerreceivedrequests/{partnerId}")
	public ResponseEntity<List<PartnerDetails>> getMyReceivedInvitations(@PathVariable("partnerId") int partnerId){
		log.info("PartnerAssociationController | getMyReceivedInvitations -  Request : partnerId - "+partnerId);
		List<PartnerDetails> lstPartnerDetails=null;
		Optional<List<PartnerAssociation>> listPartnerAssociation = partnerAssociationRepository.findMyReceivedPartnerRequests(partnerId, INVITATIONSTATUS.INVITED.getStatusId());
		if(listPartnerAssociation.isPresent()) {
			List<PartnerAssociation> lstPartnerAssociation = listPartnerAssociation.get();
			lstPartnerDetails = partnerDetailsService.getPartnerByHostPartnerId(lstPartnerAssociation);
		}

		log.info("PartnerAssociationController | getMyReceivedInvitations : Response - "+lstPartnerDetails.size());
		return new ResponseEntity<>(lstPartnerDetails,HttpStatus.OK);
	}
	//My sent partner requests
	@PostMapping(value="/partnersentrequests/{partnerId}")
	public ResponseEntity<List<PartnerDetails>> getMySentInvitations(@PathVariable("partnerId") int partnerId){
		log.info("PartnerAssociationController | getMySentInvitations - Request : partnerId - "+partnerId);
		List<PartnerDetails> lstPartnerDetails=null;
		Optional<List<PartnerAssociation>> listPartnerAssociation = partnerAssociationRepository.findPartnerByPartnerId(partnerId, INVITATIONSTATUS.INVITED.getStatusId());
		if(listPartnerAssociation.isPresent()) {
			List<PartnerAssociation> lstPartnerAssociation = listPartnerAssociation.get();
			lstPartnerDetails = partnerDetailsService.getPartnerByPartnerId(lstPartnerAssociation);
		}

		log.info("PartnerAssociationController | getMySentInvitations : Response - "+lstPartnerDetails.size());
		return new ResponseEntity<>(lstPartnerDetails,HttpStatus.OK);
	}

	//Get Partner invitation status
	@PostMapping(value="/partnerinvitationstatus")
	public ResponseEntity<PartnerAssociation> checkPartnerInvitationStatus(@RequestBody PartnerAssociation partnerAssociation){
		log.info("PartnerAssociationController | checkPartnerInvitationStatus - Request : partnerId - "+partnerAssociation);
		PartnerAssociation partnerAssociationObj=null;
		Optional<PartnerAssociation> pAssociation = partnerAssociationRepository.checkInvitationStatus(partnerAssociation.getSourcePartnerId(), 
				partnerAssociation.getAssociatedPartnerId());
		if(pAssociation.isPresent()) {
			partnerAssociationObj = pAssociation.get();
		} 
		//log.info("PartnerAssociationController | checkPartnerInvitationStatus : Response - "+PartnerUtility.convertObjectToJson(partnerAssociationObj));
		return new ResponseEntity<>(partnerAssociationObj,HttpStatus.OK);
	}

	/**
	 * New APIs with Pagination implementation	
	 */

	@PostMapping(value="/ptnrrequest}")
	public ResponseEntity<PartnerSearchResponse> fetchMyPartnerRequests(@RequestBody PartnerSearchCriteria ptnrSearchCriteria){
		log.info("PartnerAssociationController | fetchMyPartnerRequests - Request : partnerId - "+PartnerUtility.convertObjectToJson(ptnrSearchCriteria));
		PartnerSearchResponse ptnrSearchResponse=null;

		Pageable paging = PageRequest.of(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize());

		Page<PartnerDetails> pagePartnerDetails = partnerDetailsRepository.findPartnerAssociationWithPatination(Integer.parseInt(ptnrSearchCriteria.getPartnerId()), INVITATIONSTATUS.INVITED.getStatusId(),paging);
		if(pagePartnerDetails.hasContent()) {
			Integer totalPages = pagePartnerDetails.getTotalPages();
			List<PartnerDetails> lstPtnrDetails = pagePartnerDetails.getContent();
			ptnrSearchResponse = PartnerSearchResponse.builder()
					.pageble(commonUtil.buildPage(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize(),totalPages))
					.partnerDetails(lstPtnrDetails)
					.build(); 
		}

		log.info("PartnerAssociationController | fetchMyPartnerRequests : Response - ");
		return new ResponseEntity<>(ptnrSearchResponse,HttpStatus.OK);
	}

	@PostMapping(value="/myassociatedptnr")
	public ResponseEntity<PartnerSearchResponse> fetchMyAssociatedPartners(@RequestBody PartnerSearchCriteria ptnrSearchCriteria){
		log.info("PartnerAssociationController | fetchMyAssociatedPartners - Request :  - "+PartnerUtility.convertObjectToJson(ptnrSearchCriteria));
		PartnerSearchResponse ptnrSearchResponse=null;

		Pageable paging = PageRequest.of(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize());

		Page<PartnerAssociation> pagePtnrAssociateDetails = partnerAssociationRepository.findPartnerByPtnrId(Integer.parseInt(ptnrSearchCriteria.getPartnerId()), INVITATIONSTATUS.ACTIVE.getStatusId(),paging);

		if(pagePtnrAssociateDetails.hasContent()) {
			List<PartnerDetails> lstPartnerDetails = new ArrayList<>();
			int totalPages = pagePtnrAssociateDetails.getTotalPages();
			List<PartnerAssociation> lstPtnrAssociation = pagePtnrAssociateDetails.getContent();
			
			if(lstPtnrAssociation!=null) {
				//List<PartnerAssociation> lstPartnerAssociation = listPartnerAssociation.get();
				lstPartnerDetails = partnerDetailsService.getPartnerByPartnerIdAndInviteePid(lstPtnrAssociation,Integer.parseInt(ptnrSearchCriteria.getPartnerId()));
			}
			
			ptnrSearchResponse = PartnerSearchResponse.builder()
					.pageble(commonUtil.buildPage(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize(),totalPages))
					.partnerDetails(lstPartnerDetails)
					.build();  
		}else {
			//If data is not available
			ptnrSearchResponse = PartnerSearchResponse.builder()
					.pageble(commonUtil.buildPage(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize(),0))
					.partnerAssociation(new ArrayList<>())
					.build();
		}
		return new ResponseEntity<>(ptnrSearchResponse,HttpStatus.OK);
	}

	//My sent partner requests with Pagination
	@PostMapping(value="/partnersentrequests")
	public ResponseEntity<PartnerSearchResponse> fetchMySentInvitations(@RequestBody PartnerSearchCriteria ptnrSearchCriteria){
		log.info("PartnerAssociationController | fetchMySentInvitations - Request : - "+PartnerUtility.convertObjectToJson(ptnrSearchCriteria));
		PartnerSearchResponse ptnrSearchResponse=null;
		Pageable paging = PageRequest.of(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize());

		Page<PartnerAssociation> pagePartnerAssociation = partnerAssociationRepository.findPartnerByPtnrId(Integer.parseInt(ptnrSearchCriteria.getPartnerId()), INVITATIONSTATUS.INVITED.getStatusId(),paging);

		if(pagePartnerAssociation.hasContent()) {
			int totalPages = pagePartnerAssociation.getTotalPages();
			List<PartnerAssociation> lstPtnrAssociation = pagePartnerAssociation.getContent();
			ptnrSearchResponse = PartnerSearchResponse.builder()
					.pageble(commonUtil.buildPage(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize(),totalPages))
					.partnerAssociation(lstPtnrAssociation)
					.build();  
		}
		return new ResponseEntity<>(ptnrSearchResponse,HttpStatus.OK);
	}

	//My received partner requests with pagination
	@PostMapping(value="/partnerreceivedrequests")
	public ResponseEntity<PartnerSearchResponse> fetchMyReceivedInvitations(@RequestBody PartnerSearchCriteria ptnrSearchCriteria){
		log.info("PartnerAssociationController | fetchMyReceivedInvitations -  Request : partnerId - "+PartnerUtility.convertObjectToJson(ptnrSearchCriteria));
		PartnerSearchResponse ptnrSearchResponse=null;
		Pageable paging = PageRequest.of(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize());

		Page<PartnerAssociation> pagePartnerAssociation = partnerAssociationRepository.findMyReceivedPartnerRequests(Integer.parseInt(ptnrSearchCriteria.getPartnerId()), INVITATIONSTATUS.INVITED.getStatusId(),paging);

		if(pagePartnerAssociation.hasContent()) {
			int totalPages = pagePartnerAssociation.getTotalPages();
			List<PartnerAssociation> lstPtnrAssociation = pagePartnerAssociation.getContent();
			ptnrSearchResponse = PartnerSearchResponse.builder()
					.pageble(commonUtil.buildPage(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize(),totalPages))
					.partnerAssociation(lstPtnrAssociation)
					.build();  
		}
		return new ResponseEntity<>(ptnrSearchResponse,HttpStatus.OK);
	}

	@GetMapping(value="/ptnractivecount")
	public ResponseEntity<Long> partnerActiveCount(@PathVariable final Integer partnerId)
	{
		log.info("PartnerDetailsController | partnerActiveCount - Request : partnerId - "+partnerId);
		Long count = partnerAssociationRepository.countByPartnerIdAndStatus(partnerId, INVITATIONSTATUS.ACTIVE.getStatusId());
		return new ResponseEntity<>(count,HttpStatus.OK);
	}
	
	private String getPartnerISAId(Integer partnerId) {
		String isaId=null;
		Optional<PartnerOrgDetails> partnerOrgDetailsRepo = partnerOrgDetailsRepository.findByPartnerId(partnerId);
		PartnerOrgDetails partnerOrgDetails=null;
		if(partnerOrgDetailsRepo.isPresent()) {
			partnerOrgDetails = partnerOrgDetailsRepo.get();
			isaId = partnerOrgDetails.getEdiIsaId();
		}
		return isaId;
	}

	private String getExternalPartnerISAId(Integer externalPartnerId) {
		String isaId=null;
		Optional<PartnerOrgDetails> partnerOrgDetailsRepo = partnerOrgDetailsRepository.findByExternalPartnerId(externalPartnerId);
		PartnerOrgDetails partnerOrgDetails=null;
		if(partnerOrgDetailsRepo.isPresent()) {
			partnerOrgDetails = partnerOrgDetailsRepo.get();
			isaId = partnerOrgDetails.getEdiIsaId();
		}
		return isaId;
	}
}