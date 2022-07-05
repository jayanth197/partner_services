package com.partner.controller.sso;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.config.PartnerConfigProperties;
import com.partner.entity.PartnerUserDetail;
import com.partner.entity.User;
import com.partner.entity.UserToken;
import com.partner.jwt.config.JwtTokenUtil;
import com.partner.model.ApiResponse;
import com.partner.model.Login;
import com.partner.service.LoginService;
import com.partner.service.sso.AuthenticationService;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController{

	private AuthenticationManager authenticationManager;
	private JwtTokenUtil jwtTokenUtil;
	private AuthenticationService authenticationService;
	private CommonUtil commonUtil;
	private LoginService loginService;
	private PartnerConfigProperties partnerConfigProperties;
	
	public AuthenticationController(AuthenticationManager authenticationManager,
			JwtTokenUtil jwtTokenUtil,
			AuthenticationService authenticationService,
			CommonUtil commonUtil,
			LoginService loginService,
			PartnerConfigProperties partnerConfigProperties) {
		
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
		this.authenticationService = authenticationService;
		this.commonUtil = commonUtil;
		this.loginService = loginService;
		this.partnerConfigProperties = partnerConfigProperties;
	}
	
		
	/**
	 * Generate authentication token using JWT;  First validate authentication; Check credentials with IPAddress(2FA); If both are satisifed then 
	 * 			generate jwt token to access APIs
	 * @param request
	 * @param loginUser
	 * @return
	 */
	@PostMapping(value = "/token")
	public ResponseEntity<ApiResponse> authenticate(HttpServletRequest request,@RequestBody Login loginUser){
		Object userObj=null;
		String token=null;
		ApiResponse apiResponse=null;
		try {
			String ipAddress = commonUtil.fetchIPAddress(request);
			log.info("AuthenticationController : Authenticate {} "+ipAddress,PartnerUtility.convertObjectToJson(loginUser));
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUserName(), loginUser.getPassword()));
			log.info("Authentication :: "+PartnerUtility.convertObjectToJson(authentication));
			PartnerUserDetail partnerUserDetail = mapToPartnerUserDetails(loginUser);
			userObj = loginService.findByUser(partnerUserDetail, ipAddress,"TOKEN");
			if(userObj instanceof User) {
				User user = (User) userObj; 
				token = jwtTokenUtil.generateToken(user);
				apiResponse = ApiResponse.builder()
						.statusCode(ResponseCodes.SUCCESS_CODE)
						.statusMessage(ResponseCodes.SUCCESS)
						.authToken(token)
						.result(user)
						.build();
				
				UserToken userToken = UserToken.builder()
						.userId(user.getUserId())
						.token(token)
						.createdDate(PartnerUtility.getCurrentDateTime())
						.createdBy(""+user.getUserId())
						.status(1)
						.build();
				
				authenticationService.saveToken(userToken);
				
			}else if(userObj instanceof ApiResponse){
				apiResponse = (ApiResponse) userObj;
			}
			return new ResponseEntity<>(apiResponse,HttpStatus.OK);
		} catch (AuthenticationException exception) {
			log.info("AuthenticationController : Authenticate {} {}",exception);
			commonUtil.saveErrorLog(exception);
			apiResponse = ApiResponse.builder()
					.statusCode(ResponseCodes.INVALID_LOGIN_CREDENTIALS)
					.statusMessage(partnerConfigProperties.loginFailMessage)
					.build();
			return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
		}
	}

	
	/**
	 * Mapping user object which is received from Request into PArtnerUserDetails object as findByUser() method of LoginService expects the same
	 * @param user
	 * @return
	 */
	private PartnerUserDetail mapToPartnerUserDetails(Login user) {
		return PartnerUserDetail.builder()
				.emailAddress(user.getUserName())
				.password(user.getPassword())
				.build();
	}
	
	/**
	 * Validate Token passed by UI
	 * @param token
	 * @return
	 */
	@GetMapping("/validate/{token}")
	public ResponseEntity<List<Object>> validateToken(@PathVariable final String token){
		log.info("AuthenticationController : validateToken {} ",token);
		List<Object> lstObject = authenticationService.findByToken(token);
		return new ResponseEntity<>(lstObject,HttpStatus.OK);
	}
}
