package com.partner.service.sso;

import com.partner.model.ApiResponse;

public interface TwoFactorAuthService {
	ApiResponse validateSecurityCode(String email,int securityCode,String ipAddress);
	ApiResponse resendSecurityCode(String email,String source);
	ApiResponse getFetchSecurityCode(String email);
}
