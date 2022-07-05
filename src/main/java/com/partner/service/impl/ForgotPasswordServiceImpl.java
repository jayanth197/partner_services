package com.partner.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.partner.entity.User;
import com.partner.model.ApiResponse;
import com.partner.model.ChangePassword;
import com.partner.repository.UserRepository;
import com.partner.service.ForgotPasswordService;
import com.partner.service.UserService;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService{

	private UserRepository userRepository;
	private UserService userService;

	public ForgotPasswordServiceImpl(UserRepository userRepository,
			UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}

	@Override
	public ApiResponse validateOtp(String email,String password,Integer otp) {
		log.info("TwoFactorAuthServiceImpl - validateSecurityCode");
		ApiResponse apiResponse = null;
		Optional<User> userOpt = userRepository.findByEmailAddressAndSecurityCode(email,otp);
		if(userOpt.isPresent()) {
			// Change the password
			ChangePassword changePassword = ChangePassword.builder().email(email).currentPassword(password).build();
			apiResponse = userService.changePassword(changePassword);
			return apiResponse;
		}else{
			return ApiResponse.builder().statusCode(ResponseCodes.ERROR_CODE).statusMessage(ResponseCodes.FAIL).build();
		}
	}

}
