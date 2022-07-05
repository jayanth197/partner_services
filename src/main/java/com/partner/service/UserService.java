package com.partner.service;

import java.util.List;

import com.partner.entity.User;
import com.partner.model.ApiResponse;
import com.partner.model.ChangePassword;


public interface UserService {
	ApiResponse saveUser(User user);
	List<User> findAll();
	ApiResponse validateUser(String userName);
	ApiResponse changePassword(ChangePassword changePassword);
	List<User> findAllByPartnerId(int partnerId);
}
