package com.partner.model;

import java.io.Serializable;

import javax.persistence.Transient;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePassword implements Serializable {
	private static final long serialVersionUID = -4899303360071611470L;
	private String email;
	private String previousPassword;
	private String currentPassword;
	private String createdBy;
	private int partnerId;
	private Integer otp;
	@Transient
	private String action;
}
