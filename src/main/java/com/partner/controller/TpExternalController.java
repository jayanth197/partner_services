
package com.partner.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.config.PartnerConfigProperties;
import com.partner.entity.PartnerOrgDetails;
import com.partner.entity.TpExternal;
import com.partner.enums.PARTNERTYPE;
import com.partner.model.ApiResponse;
import com.partner.model.PartnerSearchCriteria;
import com.partner.model.PartnerSearchResponse;
import com.partner.repository.PartnerOrgDetailsRepository;
import com.partner.repository.TpExternalRepository;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
public class TpExternalController{

	@Autowired
	private TpExternalRepository tpexternalRepository;
	
	@Autowired
	private PartnerOrgDetailsRepository partnerOrgDetailsRepository;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private PartnerConfigProperties partnerConfigProperties;
	
	@PostMapping(value="/tpexternal")
	public ResponseEntity<ApiResponse> saveTpexternal(@RequestBody TpExternal tpexternal) {
		log.info("TpExternalController | saveTpexternal - Request : "+PartnerUtility.convertObjectToJson(tpexternal));
		ApiResponse apiResponse=null;
		tpexternal.setCreatedDate(PartnerUtility.getCurrentDateTime());
		TpExternal tpExternalObj = tpexternalRepository.save(tpexternal);

		//Prepare PartnerOrganization details
		PartnerOrgDetails partnerOrgDetails = PartnerOrgDetails.builder()
				.ediIsaId(tpexternal.getEdiIsaId())
				.ediGsQualifier(tpexternal.getEdiGsQualifier())
				.ediGsId(tpexternal.getEdiGsId())
				.ediIsaQualifier(tpexternal.getEdiIsaQualifier())
				.companyName(tpexternal.getDetails())
				.websiteUrl(tpexternal.getWebsite())
				.partnerType(PARTNERTYPE.EXTERNAL_PARTNER.getValue())
				.externalPartnerId(tpExternalObj.getId())
				.createdBy(String.valueOf(tpexternal.getOwnerPartnerID()))
				.build();
		partnerOrgDetailsRepository.save(partnerOrgDetails);
		apiResponse = ApiResponse.builder().statusCode(ResponseCodes.SUCCESS).statusMessage(partnerConfigProperties.externalSuccess).build();
		log.info("TpExternalController | saveTpexternal : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	@PutMapping(value="/tpexternal")
	public ResponseEntity<ApiResponse> updateTpexternal(@RequestBody TpExternal tpexternal) {
		log.info("TpExternalController | updateTpexternal - Request : "+PartnerUtility.convertObjectToJson(tpexternal));
		ApiResponse apiResponse=null;
		tpexternal.setUpdatedDate(PartnerUtility.getCurrentDateTime());
		tpexternalRepository.save(tpexternal);
		PartnerOrgDetails partnerOrgDtlsObj = getPartnerOrgDetails(tpexternal.getId());
		if(partnerOrgDtlsObj!=null) {
			partnerOrgDtlsObj.setEdiGsId(tpexternal.getEdiGsId());
			partnerOrgDtlsObj.setEdiGsQualifier(tpexternal.getEdiGsQualifier());
			partnerOrgDtlsObj.setEdiIsaId(tpexternal.getEdiIsaId());
			partnerOrgDtlsObj.setEdiIsaQualifier(tpexternal.getEdiIsaQualifier());
			partnerOrgDetailsRepository.save(partnerOrgDtlsObj);
		}
		apiResponse = ApiResponse.builder().statusCode(ResponseCodes.SUCCESS).statusMessage("External Partner updated").build();

		log.info("TpExternalController | updateTpexternal  : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	@DeleteMapping(value="/tpexternal/{id}")
	public ResponseEntity<ApiResponse> deleteTpexternal(@PathVariable("id") Integer id) {
		log.info("Tpexternal Controller | deleteTpexternal - Request :id - "+id);
		ApiResponse apiResponse=null;
		tpexternalRepository.deleteById(id);
		PartnerOrgDetails partnerOrgDtlsObj = getPartnerOrgDetails(id);
		if(partnerOrgDtlsObj!=null) {
			partnerOrgDetailsRepository.deleteById(partnerOrgDtlsObj.getPartnerOrgDetailsId());
		}
		apiResponse = ApiResponse.builder().statusCode(ResponseCodes.SUCCESS).statusMessage("External Partner deleted").build();

		log.info("TpExternalController | deleteTpexternal : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK);
	}

	@PostMapping(value="/tpexternal/{partnerId}")
	public ResponseEntity<List<TpExternal>> getAllTpexternal(@PathVariable("partnerId") int partnerId) {
		log.info("TpexternalController | getAllTpexternal - Request : partnerId - "+partnerId);
		List<TpExternal> lstTpExternal=new ArrayList<>();
		List<TpExternal> listTpExternal=new ArrayList<>();

		Optional<List<TpExternal>> listParnerAssocation = tpexternalRepository.findByOwnerPartnerID(partnerId);
		if(listParnerAssocation.isPresent()) {
			lstTpExternal = listParnerAssocation.get();
		}

		Iterator<TpExternal> iterator = lstTpExternal.iterator();
		while(iterator.hasNext()){
			TpExternal tpExternalObj = iterator.next();
			PartnerOrgDetails pOrgDetails = getPartnerOrgDetails(tpExternalObj.getId());
			if(pOrgDetails!=null) {
				tpExternalObj.setEdiGsId(pOrgDetails.getEdiGsId());
				tpExternalObj.setEdiGsQualifier(pOrgDetails.getEdiGsQualifier());
				tpExternalObj.setEdiIsaId(pOrgDetails.getEdiIsaId());
				tpExternalObj.setEdiIsaQualifier(pOrgDetails.getEdiIsaQualifier());
				listTpExternal.add(tpExternalObj);
			}else {
				listTpExternal.add(tpExternalObj);
			}
		}

		log.info("TpExternalController | getAllTpexternal : Response - "+listTpExternal.size());
		return new ResponseEntity<>(listTpExternal,HttpStatus.OK);
	}

	private PartnerOrgDetails getPartnerOrgDetails(Integer epId) {
		Optional<PartnerOrgDetails> partnerOrgDetailsRepo = partnerOrgDetailsRepository.findByExternalPartnerId(epId);
		PartnerOrgDetails partnerOrgDetails=null;
		if(partnerOrgDetailsRepo.isPresent()) {
			partnerOrgDetails = partnerOrgDetailsRepo.get();
		}
		return partnerOrgDetails;
	}

	/**
	 * with pagination
	 * @param partnerId
	 * @return
	 */
	@PostMapping(value="/tpext")
	public ResponseEntity<PartnerSearchResponse> fetchAllTpexternal(@RequestBody PartnerSearchCriteria partnerSearchCriteria) {
		log.info("TpexternalController | fetchAllTpexternal - Request : -  "+ PartnerUtility.convertObjectToJson(partnerSearchCriteria));
		PartnerSearchResponse ptnrSearchResponse=null;
		List<TpExternal> lstTpExternal=new ArrayList<>();
		List<TpExternal> listTpExternal=new ArrayList<>();
		int totalPages=0;

		Pageable paging = PageRequest.of(partnerSearchCriteria.getPageNo(),partnerSearchCriteria.getPageSize());

		Page<TpExternal> pageTpExternal = tpexternalRepository.findByOwnerPartnerID(Integer.parseInt(partnerSearchCriteria.getPartnerId()),paging);

		if(pageTpExternal.hasContent()) {
			totalPages = pageTpExternal.getTotalPages();
			lstTpExternal = pageTpExternal.getContent();
		}

		Iterator<TpExternal> iterator = lstTpExternal.iterator();
		while(iterator.hasNext()){
			TpExternal tpExternalObj = iterator.next();
			PartnerOrgDetails pOrgDetails = getPartnerOrgDetails(tpExternalObj.getId());
			if(pOrgDetails!=null) {
				tpExternalObj.setEdiGsId(pOrgDetails.getEdiGsId());
				tpExternalObj.setEdiGsQualifier(pOrgDetails.getEdiGsQualifier());
				tpExternalObj.setEdiIsaId(pOrgDetails.getEdiIsaId());
				tpExternalObj.setEdiIsaQualifier(pOrgDetails.getEdiIsaQualifier());
				listTpExternal.add(tpExternalObj);
			}else {
				listTpExternal.add(tpExternalObj); 
			}

			ptnrSearchResponse = PartnerSearchResponse.builder()
					.pageble(commonUtil.buildPage(partnerSearchCriteria.getPageNo(),partnerSearchCriteria.getPageSize(),totalPages))
					.tpExternal(listTpExternal)
					.build(); 
		}

		if(listTpExternal.isEmpty()) {

			ptnrSearchResponse = PartnerSearchResponse.builder()
					.pageble(commonUtil.buildPage(partnerSearchCriteria.getPageNo(),partnerSearchCriteria.getPageSize(),totalPages))
					.tpExternal(listTpExternal)
					.build(); 
		}
		
		log.info("TpExternalController | getAllTpexternal : Response - "+listTpExternal.size());
		return new ResponseEntity<>(ptnrSearchResponse,HttpStatus.OK);
	}

	private PartnerOrgDetails getPartnerOrgDetails1(Integer epId) {
		Optional<PartnerOrgDetails> partnerOrgDetailsRepo = partnerOrgDetailsRepository.findByExternalPartnerId(epId);
		PartnerOrgDetails partnerOrgDetails=null;
		if(partnerOrgDetailsRepo.isPresent()) {
			partnerOrgDetails = partnerOrgDetailsRepo.get();
		}
		return partnerOrgDetails;
	}


}
