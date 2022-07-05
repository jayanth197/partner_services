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

@Table(name="error_log")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorLog implements Serializable{
	private static final long serialVersionUID = 6802960275302676183L;

	@Id
	@Column(name="error_log_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int errorLogId;
	
	@Column(name="reference_no")
	private String referenceNo;
	
	@Column(name="file_name")
	private String fileName;
	
	@Column(name="class_name")
	private String className;
	
	@Column(name="fun_name")
	private String funName;
	
	@Column(name="error_description")
	private String errorDescription;
	
	@Column(name="exception")
	private String exception;
	
	@Column(name="tpid")
	private String partnerId;
	
	@Column(name="bpi_log_Id")
	private String bpiLogId;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="created_by")
	private String createdBy;
	
}
