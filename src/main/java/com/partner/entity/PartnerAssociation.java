package com.partner.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="tplink")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PartnerAssociation implements Serializable{
	private static final long serialVersionUID = -7397310137843374854L;
	@Id
	@Column(name="link_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int associationId;
	
	@Column(name="host_pid")
	private int sourcePartnerId;
	
	@Column(name="invitee_pid")
	private int associatedPartnerId;
	
	@Column(name="reject_reason")
	private String rejectReason;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="created_by")
	private String createdBy;

	@Column(name="updated_date")
	private String updatedDate;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="status_id")
	private Integer status;
	
	@Transient
	private String action;
}
