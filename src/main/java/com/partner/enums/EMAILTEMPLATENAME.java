package com.partner.enums;

public enum EMAILTEMPLATENAME {
	
	WELCOME_EMAIL_TEMPLATE("WELCOME"),
	TWO_FACTOR_AUTHENTICATION_TEMPLATE("TWO_FACTOR_AUTH"),
	FORGOT_PASS_EMAIL_TEMPLATE("FORGOT_PASSWORD"),
	PARTNER_INVITATION("PARTNER_INVITATION"),
	CHANGE_PASSWORD_TEMPLATE("CHANGE_PASSWORD"),
	DEMO_REQUEST_SUPPORT("DEMO_REQUEST_SUPPORT"),
	DEMO_REQUEST_CUSTOMER("DEMO_REQUEST_CUSTOMER");
	
	private final String emailTempateName;
	
	private EMAILTEMPLATENAME(String emailTempateName) {
		this.emailTempateName = emailTempateName;
	}
	
	public String getEmailTempateName() {
		return this.emailTempateName;
	}
	
}
