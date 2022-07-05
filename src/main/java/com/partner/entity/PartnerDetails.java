package com.partner.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="tpd")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PartnerDetails implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="tpid")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer partnerId;

	@Column(name="first_name")
	private String firstName;

	@Column(name="last_name")
	private String lastName;

	@Column(name="middle_name")
	private String middleName;

	@Column(name="primary_email")
	private String partnerEmail;

	@Column(name="primary_telephone")
	private String primaryTelephone;

	@Column(name="address_line_1")
	private String addressLine1;

	@Column(name="address_line_2")
	private String addressLine2;

	@Column(name="state_name")
	private String stateName;

	@Column(name="city_name")
	private String cityName;

	@Column(name="zip_code")
	private Integer zipCode;

	@Column(name="country")
	private String country;

	@Column(name="access_level_id")
	private Integer accessLevelId;

	@Column(name="profile_pic_name")
	private String profilePicName;
	
	@Column(name="created_date")
	private String createdDate;

	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="updated_date")
	private String updatedDate;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="status_id")
	private Integer statusId;

	@Transient
	private String profilePicData;
	
	@Transient
	private String ipAddress;
	
	@Transient
	private String action;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy ="partnerDetails" )
	@JsonIgnoreProperties("partnerDetails")
	private PartnerOrgDetails partnerOrgDetails;

	public void addPartnerOrgDetails(PartnerOrgDetails orgDetails) { 
		if(orgDetails == null) { return; } 
		orgDetails.setPartnerDetails(this); 
		if(partnerOrgDetails == null) { 
			partnerOrgDetails = new PartnerOrgDetails();
			partnerOrgDetails = orgDetails; 
		} 
	}
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy ="partnerDetails" )
	@JsonIgnoreProperties("partnerDetails")
	private PartnerUserAssociation partnerUserAssociation;

	public void addPartnerUserAssociation(PartnerUserAssociation pUserAssociation) { 
		if(pUserAssociation == null) { return; } 
			pUserAssociation.setPartnerDetails(this); 
		if(partnerUserAssociation == null) { 
			partnerUserAssociation = new PartnerUserAssociation();
			partnerUserAssociation = pUserAssociation; 
		} 
	}
}
