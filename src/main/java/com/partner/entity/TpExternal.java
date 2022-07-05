package com.partner.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tpexternal")
@Entity
public class TpExternal implements Serializable{
	private static final long serialVersionUID = 6074099236714020958L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="owner_partnerID")
	private int ownerPartnerID;
	
	@Column(name="name")
	private String name;
	
	@Column(name="details")
	private String details;
	
	@Column(name="website")
	private String website;
	
	@Column(name="line1")
	private String line1;
	
	@Column(name="line2")
	private String line2;
	
	@Column(name="zipcode")
	private String zipcode;
	
	@Column(name="city")
	private String city;
	
	@Column(name="state")
	private String state;
	
	@Column(name="country")
	private String country;
	
	@Column(name="poc_fn")
	private String pocfn;
	
	@Column(name="poc_mi")
	private String pocmi;
	
	@Column(name="poc_ln")
	private String pocln;
	
	@Column(name="poc_phone")
	private String pocphone;
	
	@Column(name="poc_email")
	private String pocemail;
	
	@Column(name="fax")
	private String fax;
	
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
	private String ediIsaId;
	
	@Transient
	private String ediIsaQualifier;
	
	@Transient
	private String ediGsId;
	
	@Transient
	private String ediGsQualifier;

	
}
