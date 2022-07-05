package com.partner.util;

public class ResponseCodes {

	private ResponseCodes() {  }
	
	public static final String SUCCESS_CODE="0000";
	public static final String ERROR_CODE="9999";
	public static final String FAIL="FAIL";
	public static final String SUCCESS="SUCCESS";
	public static final String INVALID_USER_CODE="9001";
	public static final String INVALID_LOGIN_CREDENTIALS="9002";
	public static final String DUPLICATE_PARTNER="9003";
	public static final String DUPLICATE_EMAIL_ADDRESS="9004";
	public static final String SEARCH_PARTNER_FAIL_CODE = "9005";
	public static final String EXPIRED_2FA_SECURITY_CODE = "9006";
	public static final String INVALID_2FA_SECURITY_CODE = "9007";
	public static final String EMAIL_NOT_FOUND = "9008";
	public static final String INVALID_TOKEN="9009";
	public static final String INVALID_API_KEY="9010";
}
