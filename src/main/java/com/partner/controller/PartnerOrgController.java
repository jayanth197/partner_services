package com.partner.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.entity.PartnerOrgDetails;
import com.partner.model.ApiResponse;
import com.partner.repository.PartnerOrgDetailsRepository;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
public class PartnerOrgController {

	private PartnerOrgDetailsRepository partnerOrgDetailsRepository;

	public PartnerOrgController(PartnerOrgDetailsRepository partnerOrgDetailsRepository) {
		this.partnerOrgDetailsRepository = partnerOrgDetailsRepository;
	}

	@PostMapping("/ptnrorg")
	public ResponseEntity<ApiResponse> savePartnerOrgDetails(@RequestBody PartnerOrgDetails partnerOrgDetails){
		log.info("PartnerOrgController - savePartnerOrgDetails : Request received - "+PartnerUtility.convertObjectToJson(partnerOrgDetails));
		partnerOrgDetailsRepository.save(partnerOrgDetails);
		ApiResponse apiResponse = ApiResponse.builder().statusCode(ResponseCodes.SUCCESS_CODE).statusMessage(ResponseCodes.SUCCESS).build();
		log.info("PartnerOrgController - savePartnerOrgDetails : Request received - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	//Fetch Partner Org details by ISAID
	@GetMapping("/ptnrorgisaid/{isaId}")
	public ResponseEntity<PartnerOrgDetails> findOrgDtlsByIsaId(@PathVariable final String isaId) {
		PartnerOrgDetails partnerOrgDetails=null;
		Optional<PartnerOrgDetails> optPartnerOrgDetails = partnerOrgDetailsRepository.findByEdiIsaId(isaId);
		if(optPartnerOrgDetails.isPresent()) {
			partnerOrgDetails = optPartnerOrgDetails.get();
		}
		return new ResponseEntity<>(partnerOrgDetails,HttpStatus.OK);
	}


	//Fetch Partner Org details by PartnerId
	@GetMapping("/ptnrorgpid/{partnerId}")
	public ResponseEntity<PartnerOrgDetails> findOrgDtlsByPartnerId(@PathVariable final Integer partnerId) {
		PartnerOrgDetails partnerOrgDetails=null;
		Optional<PartnerOrgDetails> optPartnerOrgDetails = partnerOrgDetailsRepository.findByPartnerId(partnerId);
		if(optPartnerOrgDetails.isPresent()) {
			partnerOrgDetails = optPartnerOrgDetails.get();
		}
		return new ResponseEntity<>(partnerOrgDetails,HttpStatus.OK);
	}

	//Fetch Partner Org details by PartnerId
	@GetMapping("/extptnrorg/{partnerId}")
	public ResponseEntity<PartnerOrgDetails> findOrgDtlsByExternalPartnerId(@PathVariable final Integer partnerId) {
		PartnerOrgDetails partnerOrgDetails=null;
		Optional<PartnerOrgDetails> optPartnerOrgDetails = partnerOrgDetailsRepository.findByExternalPartnerId(partnerId);
		if(optPartnerOrgDetails.isPresent()) {
			partnerOrgDetails = optPartnerOrgDetails.get();
		}
		return new ResponseEntity<>(partnerOrgDetails,HttpStatus.OK);
	}
}
