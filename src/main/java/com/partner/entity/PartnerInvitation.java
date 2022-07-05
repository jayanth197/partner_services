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
@Table(name="tpinv")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PartnerInvitation implements Serializable{
	private static final long serialVersionUID = -8956723513554509360L;

	@Id
	@Column(name="tpinv_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tpInvId;
	
	@Column(name="organization_name")
	private String organizationName;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="email")
	private String email;
	
	@Column(name="phone")
	private String phone;
	
	@Column(name="sender_name")
	private String senderName;
	
	@Column(name="sender_organization")
	private String senderOrganization;
	
	@Column(name="sender_partner_id")
	private String senderPartnerId;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="status")
	private Integer status;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="updated_date")
	private String updatedDate;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Transient
	private String action;
}
