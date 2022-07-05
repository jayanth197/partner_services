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
@Table(name="tpud")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PartnerUserDetail implements Serializable{

	private static final long serialVersionUID = 3157133513372733540L;
	@Id
	@Column(name="tpid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int partnerUserId;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="middle_initial")
	private String middleName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="contact_number")
	private String contactNumber;
	
	@Column(name="user_email")
	private String emailAddress;
	
	@Column(name="password")
	private String password;
	
	@Column(name="zip_code")
	private String zipCode;
	
	@Column(name="access_level_id")
	private int accessLevelId;
	
	@Column(name="utid")
	private int userTypeId;
	
	@Column(name="details_required")
	private boolean isDetailsRequired=true;
	
	@Column(name="status_id")
	private int statusId;
	
	@Column(name="organization_name")
	private String organizationName;
	
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
	
	@Transient
	private String actualPassword;
	
	@Transient
	private String action;
	
	
}
