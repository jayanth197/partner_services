package com.partner.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="tpts")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PartnerSubscriptions implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="tp_subs_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int partnerSubcriptionId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "tpid")
	@JsonIgnoreProperties("lstPartnerSubscriptions")
	private PartnerDetails partnerDetails;
	
	@Column(name="source_file_type_id")
	private int sourceFileTypeId;
	
	@Column(name="destination_file_type_id")
	private int destinationFileTypeId;

	@Column(name="transaction_type_id")
	private int transactionTypeId;

	@Column(name="sender_format")
	private String senderFormat;

	@Column(name="sender_standard")
	private String senderStandard;

	@Column(name="sender_version")
	private String senderVersion;

	@Column(name="receiver_format")
	private String receiverFormat;

	@Column(name="receiver_standard")
	private String receiverStandard;

	@Column(name="receiver_version")
	private String receiverVersion;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="updated_date")
	private String updatedDate;
	
	@Column(name="updated_by")
	private String updatedBy;

	@Column(name="status")
	private Integer status;
}
