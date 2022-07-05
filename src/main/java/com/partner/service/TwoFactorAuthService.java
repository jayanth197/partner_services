package com.partner.service;

import com.partner.model.ApiResponse;

public interface TwoFactorAuthService {
	ApiResponse validateSecurityCode(String email,int securityCode,String ipAddress);
	ApiResponse resendSecurityCode(String email);
}
