package com.partner.service.sso;

import java.util.List;

import com.partner.entity.UserToken;
import com.partner.model.ApiResponse;

public interface AuthenticationService {
    ApiResponse saveToken(UserToken userToken);
    List<Object> findByToken(String token);
}
