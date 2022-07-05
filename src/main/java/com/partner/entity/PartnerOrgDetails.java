package com.partner.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

@Table(name = "tpod")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PartnerOrgDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "tp_org_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int partnerOrgDetailsId;

	@Column(name = "company_name")
	private String companyName;

	@Column(name = "website_url")
	private String websiteUrl;
	
	@Column(name = "facebook_url")
	private String facebookUrl;
	
	@Column(name = "linkedin_url")
	private String linkedInUrl;
	
	@Column(name = "instagram_url")
	private String instagramUrl;
	
	@Column(name = "twitter_url")
	private String twitterUrl;

	@Column(name = "company_image_name")
	private String companyImageName;

	@Column(name = "company_bg_image_name")
	private String companyBgImageName;

	@Column(name = "company_description")
	private String companyDescription;

	@Column(name = "company_size")
	private String companySize;

	@Column(name = "company_type")
	private String companyType;

	@Column(name = "company_about")
	private String companyAbout;
	
	@Column(name = "test_isa")
	private String testIsa;
	
	@Column(name = "test_gs")
	private String testGs;
	
	@Column(name = "test_qualifier")
	private String testQualifier;

	@Column(name = "edi_isa_id")
	private String ediIsaId;

	@Column(name = "edi_isa_qualifier")
	private String ediIsaQualifier;

	@Column(name = "edi_gs_id")
	private String ediGsId;

	@Column(name = "edi_gs_qualifier")
	private String ediGsQualifier;

	@Column(name = "partner_type")
	private String partnerType;

	@Column(name = "external_partner_id")
	private Integer externalPartnerId;

	@Column(name="created_date")
	private String createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name="updated_date")
	private String updatedDate;
	
	@Column(name="updated_by")
	private String updatedBy;

	@Column(name="status")
	private Integer status;
	
	@Transient
	private String companyLogoImgData;
	
	@Transient
	private String companyBgImgData;

	@OneToOne
	@JoinColumn(name = "partner_id")
	@JsonIgnoreProperties("partnerOrgDetails")
	private PartnerDetails partnerDetails;
}
