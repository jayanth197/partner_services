package com.partner.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partner.config.PartnerConfigProperties;
import com.partner.entity.AuditLog;
import com.partner.model.ApiResponse;
import com.partner.repository.AuditLogRepository;
import com.partner.service.AuditLogService;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

@Service
public class AuditLogServiceImpl implements AuditLogService {



	@Autowired
	AuditLogRepository auditLogRespository;

	@Autowired
	AuditLogService auditLogService;

	@Autowired
	PartnerConfigProperties partnerConfigProperties;

	@Override
	public ApiResponse saveAuditLog(AuditLog auditLog) {

		AuditLog.builder()
		.partnerId(auditLog.getPartnerId())
		.userId(auditLog.getUserId())
		.actionType(auditLog.getActionType())
		.action(auditLog.getAction())
		.createdDate(PartnerUtility.getCurrentDateTime())
		.createdBy(auditLog.getCreatedBy());

		auditLogRespository.save(auditLog);
		return ApiResponse.builder().statusCode(ResponseCodes.SUCCESS_CODE).statusMessage(partnerConfigProperties.auditSuccess).build();
	}


	@Override
	public List<AuditLog> getAllByLogId(Integer logId) {
		Optional<List<AuditLog>> optAuditLogList = auditLogRespository.findByLogId(logId);
		if(optAuditLogList.isPresent()) {
			return optAuditLogList.get();
		}else {
			return new ArrayList<>();
		}
	}
}





