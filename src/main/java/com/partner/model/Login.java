package com.partner.model;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class Login implements Serializable{
	private static final long serialVersionUID = 2644811950519949292L;
	private String userName;
	private String password;
}
