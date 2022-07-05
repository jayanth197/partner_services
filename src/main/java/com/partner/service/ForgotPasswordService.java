package com.partner.service;

import com.partner.model.ApiResponse;

public interface ForgotPasswordService {
	ApiResponse validateOtp(String email,String password,Integer otp);
}
