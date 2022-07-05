package com.partner.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partner.entity.CustomerRequest;
import com.partner.entity.User;
import com.partner.model.ApiResponse;
import com.partner.model.CustomerModuleRequest;
import com.partner.repository.CustomerRequestRepository;
import com.partner.service.CustomerRequestService;
import com.partner.service.SendMail;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerRequestServiceImpl implements CustomerRequestService {

	@Autowired
	CustomerRequestRepository customerRequestRepository;
	
	@Autowired
	SendMail sendMail;
	
	@Override
	public ApiResponse saveCustomerRequest(CustomerModuleRequest customerModuleRequest) {
		ApiResponse response = null;
		log.info("Save customer Request");
		try {
			CustomerRequest customRequest = customerModuleRequest.getCustomerRequest();
			User user = customerModuleRequest.getUser();
			log.info("before save");
			customerRequestRepository.save(customRequest);
			String request = " Request to access modules has been raised:" + "<br/>First Name: " + user.getFirstName()
					+ "<br/>Last Name: " + user.getLastName() + "<br/>Organization Name: " + user.getOrganization()
					+ "<br/>Email: " + user.getEmailAddress() + "<br/>Phone: " + user.getContactNumber()
					+ "<br/>Modules: " + customRequest.getModules() + "<br/> Comments: " + customRequest.getComments();
			sendMail.sendEmailToSales(request, "Module Request");
			response = new ApiResponse();
			response.setStatusCode(ResponseCodes.SUCCESS);
		} catch (Exception e) {
			log.info("Exception"+e);
			// TODO: handle exception
		}
		return response;
	}

}
