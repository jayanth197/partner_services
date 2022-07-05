package com.partner.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="email_template")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailTemplate implements Serializable{
	
	private static final long serialVersionUID = -8822423521422439259L;
	
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	@Column(name="email_template_id")
	@JsonProperty("id")
	private int id;
	
	@Column(name="source")
	private String source;
	
	@Column(name="type")
	private String templateType;
	
	@Column(name="name")
	private String templateName;
	
	@Column(name="subject")
	private String templateSubject;
	
	@Column(name="body")
	private String templateBody;
	
	@Column(name="description")
	private String description;
	
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
