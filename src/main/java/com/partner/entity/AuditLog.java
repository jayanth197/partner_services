package com.partner.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="audit_log")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog implements Serializable {
	
	private static final long serialVersionUID = -3884034954387888983L;
	
	@Id
	@Column(name="log_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int logId;
	
	@Column(name="partner_id")
	private int partnerId;
	
	@Column(name="user_id")
	private String userId;
	
    @Column(name="action_type")
	private String actionType;
	
    @Column(name="action")
	private String action;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	
}

