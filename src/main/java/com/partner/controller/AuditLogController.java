package com.partner.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.entity.AuditLog;
import com.partner.repository.AuditLogRepository;
import com.partner.service.AuditLogService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
public class AuditLogController {
	
	@Autowired
	AuditLogService auditLogService;
	
	@Autowired
	AuditLogRepository auditLogRepository;
	
	@GetMapping("/audit/{parnterId}")
	public ResponseEntity<List<AuditLog>> getAuditLogByPartnerId(@PathVariable("partnerId") int partnerId)
	{
		log.info("AuditLogController | getAuditLogByPartnerId :  Request : partnerId - "+partnerId);
		List<AuditLog> listAuditLog=null;
			Optional<List<AuditLog>> lstAuditLog = auditLogRepository.findByLogId(partnerId);
			if(lstAuditLog.isPresent()) {
				listAuditLog = lstAuditLog.get();
			}
		
		log.info("AuditLogController | getAuditLogByPartnerId : Response - "+listAuditLog.size());
		return new ResponseEntity<>(listAuditLog,HttpStatus.OK);
	}

	
}