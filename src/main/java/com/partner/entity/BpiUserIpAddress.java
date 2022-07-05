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

@Entity
@Table(name="bpi_user_ip")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BpiUserIpAddress implements Serializable{
	private static final long serialVersionUID = -4951410487937213152L;

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="partner_id")
	private int partnerId;

	@Column(name="user_id")
	private int userId;

	@Column(name="ip_address")
	private String ipAddress;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="status")
	private Integer status;
}
