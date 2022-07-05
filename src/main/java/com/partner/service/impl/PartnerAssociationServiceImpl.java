package com.partner.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.partner.config.PartnerConfigProperties;
import com.partner.entity.AuditLog;
import com.partner.entity.PartnerAssociation;
import com.partner.enums.ACTIONTYPE;
import com.partner.enums.INVITATIONSTATUS;
import com.partner.model.ApiResponse;
import com.partner.repository.PartnerAssociationRepository;
import com.partner.service.PartnerAssociationService;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PartnerAssociationServiceImpl implements PartnerAssociationService{

	private PartnerAssociationRepository partnerAssociationRepository;
	private PartnerConfigProperties partnerConfigProperties;
	private AuditLogServiceImpl auditLogServiceImpl;

	public PartnerAssociationServiceImpl(PartnerAssociationRepository partnerAssociationRepository,
			PartnerConfigProperties partnerConfigProperties,
			AuditLogServiceImpl auditLogServiceImpl) {
		this.partnerAssociationRepository = partnerAssociationRepository;
		this.partnerConfigProperties = partnerConfigProperties;
		this.auditLogServiceImpl = auditLogServiceImpl;
	}

	@Override
	public ApiResponse savePartner(PartnerAssociation partnerAssociation) {
		partnerAssociation.setStatus(INVITATIONSTATUS.INVITED.getStatusId());
		partnerAssociationRepository.save(partnerAssociation);
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatusCode(ResponseCodes.SUCCESS);
		apiResponse.setStatusMessage(partnerConfigProperties.partnerAssociationSuccess);
		
		AuditLog auditLog = AuditLog.builder()
				.actionType(ACTIONTYPE.CREATE.getActiontType())
				.action(partnerAssociation.getAction())
				.createdDate(PartnerUtility.getCurrentDateTime())
				.createdBy(partnerAssociation.getCreatedBy())
				.partnerId(partnerAssociation.getSourcePartnerId())
				.build();
		
		auditLogServiceImpl.saveAuditLog(auditLog);
		
		return apiResponse;
	}

	//Active or Inactive associated partner based on previous status
	@Override
	public ApiResponse updateStatus(PartnerAssociation partnerAssociation) {
		log.info("PartnerAssociationServiceImpl :: updateStatus"+PartnerUtility.convertObjectToJson(partnerAssociation));
		ApiResponse apiResponse = new ApiResponse();
		int activeStatus=0;
		int previouStauts=0;
		//  Check current status of the associated partner and change status if it previous status is opposite
		//  Current status is Active then previous status should be InActive
		//  Current status is InActive then previous status should be Active
		if(partnerAssociation.getStatus() == INVITATIONSTATUS.INACTIVE.getStatusId()) {
			log.info("PartnerAssociationServiceImpl :: updateStatus "+partnerAssociation.getStatus());
			activeStatus = INVITATIONSTATUS.ACTIVE.getStatusId();
			previouStauts = INVITATIONSTATUS.INACTIVE.getStatusId();
			apiResponse.setStatusMessage(partnerConfigProperties.partnerStatusActive);
		}else if(partnerAssociation.getStatus() == INVITATIONSTATUS.ACTIVE.getStatusId()){
			log.info("PartnerAssociationServiceImpl :: updateStatus "+partnerAssociation.getStatus());
			activeStatus = INVITATIONSTATUS.INACTIVE.getStatusId();
			previouStauts = INVITATIONSTATUS.ACTIVE.getStatusId();
			apiResponse.setStatusMessage(partnerConfigProperties.partnerStatusActive);
		}
		//Update status of an associated Partner
		int response = partnerAssociationRepository.updateStatus(activeStatus,partnerAssociation.getSourcePartnerId(),partnerAssociation.getAssociatedPartnerId(),previouStauts);
		if(response>0) {
			log.info("PartnerAssociationServiceImpl :: updateStatus"+response);
			apiResponse.setStatusCode(ResponseCodes.SUCCESS);
		}else {
			log.info("PartnerAssociationServiceImpl :: updateStatus"+response);
			apiResponse.setStatusCode(ResponseCodes.ERROR_CODE);
		}
		
		AuditLog auditLog = AuditLog.builder()
				.actionType(ACTIONTYPE.UPDATE.getActiontType())
				.action(partnerAssociation.getAction())
				.createdDate(PartnerUtility.getCurrentDateTime())
				.createdBy(partnerAssociation.getCreatedBy())
				.partnerId(partnerAssociation.getSourcePartnerId())
				.build();
		
		auditLogServiceImpl.saveAuditLog(auditLog);
		
		
		return apiResponse;
	}

	@Override
	public ApiResponse updateInvitationStatus(PartnerAssociation partnerAssociation) {
		partnerAssociationRepository.updateInvitationStatusByPid(partnerAssociation.getStatus(),partnerAssociation.getRejectReason(), partnerAssociation.getSourcePartnerId(),partnerAssociation.getAssociationId());
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatusCode(ResponseCodes.SUCCESS);
		apiResponse.setStatusMessage("Updated status successfully");
		

		// Add update audit log - Ankitha
		AuditLog auditLog = AuditLog.builder()
				.actionType(ACTIONTYPE.UPDATE.getActiontType())
				.action(partnerAssociation.getAction())
				.createdDate(PartnerUtility.getCurrentDateTime())
				.createdBy(partnerAssociation.getCreatedBy())
				.partnerId(partnerAssociation.getSourcePartnerId())
				.build();
		
		auditLogServiceImpl.saveAuditLog(auditLog);
		
		
		return apiResponse;
	}

	@Override
	public PartnerAssociation getPartnerAssociationStatus(PartnerAssociation partnerAssociation) {
		log.info("PartnerAssociationServiceImpl :: getPartnerAssociationStatus - "+partnerAssociation.toString());
		PartnerAssociation pAssociation=null;
		Optional<PartnerAssociation> partOptional = partnerAssociationRepository.findBySourcePartnerIdAndAssociatedPartnerId(partnerAssociation.getSourcePartnerId(), partnerAssociation.getAssociatedPartnerId());
		if(partOptional.isPresent()) {
			pAssociation = partOptional.get();
			log.info("PartnerAssociationServiceImpl :: getPartnerAssociationStatus - "+pAssociation.toString());
		}else {
			log.info("PartnerAssociationServiceImpl :: getPartnerAssociationStatus - No Association found");
		}
		return pAssociation;
	}
}
