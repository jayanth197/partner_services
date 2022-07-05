package com.partner.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.partner.model.ApiResponse;
import com.partner.model.CustomerModuleRequest;

@Service
public interface CustomerRequestService {

    ApiResponse saveCustomerRequest(CustomerModuleRequest customerModuleRequest);
}