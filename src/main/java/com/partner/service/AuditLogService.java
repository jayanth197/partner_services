package com.partner.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.partner.entity.AuditLog;
import com.partner.model.ApiResponse;

@Service
public interface AuditLogService {

    ApiResponse saveAuditLog(AuditLog auditLog);
	List<AuditLog> getAllByLogId(Integer logId);
}