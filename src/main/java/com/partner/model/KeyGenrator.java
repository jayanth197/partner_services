package com.partner.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KeyGenrator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int partnerId;

	private String createdBy;

	private String updateddBy;
}
