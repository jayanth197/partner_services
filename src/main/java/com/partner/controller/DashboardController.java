package com.partner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.enums.INVITATIONSTATUS;
import com.partner.model.ApiResponse;
import com.partner.model.CustomerModuleRequest;
import com.partner.model.PartnerDashBoard;
import com.partner.repository.PartnerAssociationRepository;
import com.partner.repository.PartnerDetailsRepository;
import com.partner.repository.TpExternalRepository;
import com.partner.service.CustomerRequestService;
import com.partner.util.PartnerUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1")
public class DashboardController {

	@Autowired
	PartnerAssociationRepository partnerAssociationRepository;

	@Autowired
	TpExternalRepository tpExternalRepository;

	@Autowired
	PartnerDetailsRepository partnerDetailsRepository;
	
	CustomerRequestService customerRequestService;
	
	public DashboardController(CustomerRequestService customerRequestService) {
		this.customerRequestService = customerRequestService;
	}

	@GetMapping("/dashboard/{partnerId}")
	public ResponseEntity<PartnerDashBoard> getDashboardDetails(@PathVariable final Integer partnerId){
		log.info("DashboardController - getDashboardDetails : Request received for the partner - "+partnerId);

		long associated =  partnerAssociationRepository.countByPartnerIdAndStatus(partnerId,INVITATIONSTATUS.ACTIVE.getStatusId());
		long external = tpExternalRepository.countByOwnerPartnerID(partnerId);
		long externalInvites = partnerDetailsRepository.findMyPartnerInvitationsCount(partnerId, INVITATIONSTATUS.INVITED.getStatusId()); 
		long requests = partnerDetailsRepository.findReceivedPartnerInvitationsCount(partnerId, INVITATIONSTATUS.INVITED.getStatusId());	
		long internalInvites = partnerDetailsRepository.findMyPartnerInvitationsCount(partnerId, INVITATIONSTATUS.INVITED.getStatusId());

		PartnerDashBoard partnerDashBoard = PartnerDashBoard.builder()
				.associatedPartners(associated)
				.external(external)
				.externalInvites(externalInvites)
				.requests(requests)
				.internalInvites(internalInvites)
				.build();
		log.info("DashboardController - getDashboardDetails : Response - "+PartnerUtility.convertObjectToJson(partnerDashBoard));

		return new ResponseEntity<>(partnerDashBoard,HttpStatus.OK);
	}
	
	@PostMapping("/dashboard/raiseRequest")
	public ResponseEntity<ApiResponse> raiseRequest(@RequestBody CustomerModuleRequest customerModuleRequest){
		ApiResponse apiResponse = customerRequestService.saveCustomerRequest(customerModuleRequest);
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
		
	}
	

}
