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
import lombok.Setter;

@Entity
@Table(name="email_log")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailLog implements Serializable {
	private static final long serialVersionUID = 8991130709573604825L;
	@Id
	@Column(name="email_log_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="type")
	private String type;
	
	@Column(name="email_address")
	private String emailAddress;
	
	@Column(name="email_body")
	private String emailBody;
	
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
