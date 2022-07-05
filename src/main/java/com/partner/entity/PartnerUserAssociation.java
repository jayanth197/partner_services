package com.partner.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="tpua")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PartnerUserAssociation implements Serializable{
	
	private static final long serialVersionUID = -3079682133212771909L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="uaid")
	private int id;
	
	@Column(name="uid")
	private int userId;
	
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

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "tpid")
	@JsonIgnoreProperties("partnerUserAssociation")
	private PartnerDetails partnerDetails;
}
