package com.partner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.entity.User;
import com.partner.model.ApiResponse;
import com.partner.model.ChangePassword;
import com.partner.model.UserSearchCriteria;
import com.partner.model.UserSearchResponse;
import com.partner.repository.UserRepository;
import com.partner.service.UserService;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerUtility;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/v1")
@Slf4j
public class UserController{
	
	private UserService userService;
	
	@Autowired
	private CommonUtil commonutil;
	
	@Autowired
	UserRepository userRepository;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping(value="/ptnruser") 
	public ResponseEntity<ApiResponse>saveUser(@RequestBody User user) {
		log.info("UserController | saveUser - Request : "+PartnerUtility.convertObjectToJson(user));
		ApiResponse apiResponse = null; 
		apiResponse = userService.saveUser(user); 

		log.info("UserControllerResponse | saveUser : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK); 
	}

	@GetMapping(value="/ptnruser") 
	public ResponseEntity<List<User>> getAllUsers() {
		log.info("UserController | getAllUsers : Request received");
		List<User> lstUsers=null; 
		lstUsers = userService.findAll(); 

		log.info("UserController | getAllUsers : Response - "+lstUsers.size());
		return new ResponseEntity<>(lstUsers,HttpStatus.OK); 
	}

	@GetMapping(value="/user/{emailId}") 
	public ResponseEntity<List<User>> getUserByEmailId(@PathVariable("emailId") String emailId){ 
		log.info("UserController | getUserByEmailId - Request :emailId - "+emailId);
		List<User> lstUsers=null; 
		lstUsers = userService.findAll(); 

		log.info("UserController | getUserByEmailId : Response - "+lstUsers.size());
		return new ResponseEntity<>(lstUsers,HttpStatus.OK); 
	}

	@GetMapping(value="/ptnruser/{partnerId}") 
	public ResponseEntity<List<User>> getAllByPartnerId(@PathVariable("partnerId") String partnerId) {
		log.info("UserController | getAllByPartnerId - Request : partnerId - "+partnerId);
		List<User> lstUsers=null; 
		lstUsers = userService.findAllByPartnerId(Integer.parseInt(partnerId));

		log.info("UserController | getAllByPartnerId : Response - "+lstUsers.size());
		return new ResponseEntity<>(lstUsers,HttpStatus.OK); 
	}
	
	//getAllbyPartnerId with pagination
	
	@PostMapping(value="/pagination/ptnruser") 
	public ResponseEntity<UserSearchResponse> fetchAllByPartnerId(@RequestBody UserSearchCriteria userSearchCriteria) {
		log.info("UserController | fetchAllByPartnerId : Request : - "+PartnerUtility.convertObjectToJson(userSearchCriteria));
		UserSearchResponse userSearchResponse = null;
		
			Pageable paging = PageRequest.of(userSearchCriteria.getPageNo(),userSearchCriteria.getPageSize());
			
			Page<User> pageUser = userRepository.findAllByPartnerId(userSearchCriteria.getPartnerId(), paging);

			if(pageUser.hasContent()) {
				int totalPages = pageUser.getTotalPages();
				List<User> lstUsers = pageUser.getContent();
				userSearchResponse = UserSearchResponse.builder()
						.pageble(commonutil.buildPage(userSearchCriteria.getPageNo(),userSearchCriteria.getPageSize(),totalPages))
						.user(lstUsers)
						.build();  
			}

		return new ResponseEntity<>(userSearchResponse,HttpStatus.OK); 
	}
	

	@PostMapping(value="/forgotpwd/{email}") 
	public ResponseEntity<ApiResponse> forgotPassword(@PathVariable("email") String email){ 
		log.info("UserController | forgotPassword - Request :email - "+email);
		ApiResponse apiResponse = null; 
		apiResponse = userService.validateUser(email); 

		log.info("UserController | forgotPassword : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK); 
	}

	@PutMapping(value="/password") 
	public ResponseEntity<ApiResponse> changePassword(@RequestBody ChangePassword changePassword){ 
		log.info("UserController | changePassword - Request : "+PartnerUtility.convertObjectToJson(changePassword));
		ApiResponse apiResponse = null; 
		apiResponse = userService.changePassword(changePassword); 

		log.info("UserController | changePassword : Response - "+PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse,HttpStatus.OK); 
	}
}