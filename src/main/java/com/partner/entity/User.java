package com.partner.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="bpi_user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable{
	private static final long serialVersionUID = -1231709884127581384L;

	@Id
	@Column(name="uid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	
	@Column(name="partner_id")
	private int partnerId;
	
	@Column(name="user_name")
	private String userName;
	
	@Column(name="password")
	private String password;
	
	@NotNull(message = "First Name is mandatory")
	@Size(min = 2,max = 20,message = "{user.firstname.size}" )
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="middle_initial")
	private String middleInitial;
	
	@NotNull(message = "First Name is mandatory")
	@Size(min = 2,max = 20,message = "{user.lastname.size}" )
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="email_address")
	private String emailAddress;
	
	@Column(name="contact_number")
	private String contactNumber;
	
	@Column(name="details_required")
	private boolean isDetailsRequired;
	
	@Column(name="type")
	private String type;
	
	@Column(name="permission")
	private String permission;
	
	@Column(name="organization")
	private String organization;
	
	@Column(name="partner_user_id")
	private int partnerUserId;
	
	@Column(name="ip_address")
	private String ipAddress;
	
	@Column(name="security_code_2fa")
	private int securityCode;
	
	@Column(name="is_subscribed_partner")
	private int isSubscribedPartner;
	
	@Column(name="module_name")
	private String moduleName;
	
	@Column(name = "created_date")
	private String createdDate;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "updated_date")
	private String updatedDate;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="status")
	private Integer status;
	
	@Transient
	private String action;
	
	@Transient
	private String actualPassword;
}
