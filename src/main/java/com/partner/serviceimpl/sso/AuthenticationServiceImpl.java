package com.partner.serviceimpl.sso;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.partner.entity.PartnerUserAssociation;
import com.partner.entity.User;
import com.partner.entity.UserToken;
import com.partner.exception.InvalidTokenException;
import com.partner.model.ApiResponse;
import com.partner.repository.PartnerUserAssociationRepository;
import com.partner.repository.UserRepository;
import com.partner.repository.UserTokenRepository;
import com.partner.service.sso.AuthenticationService;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService{

	private UserTokenRepository userTokenRepository;

	private UserRepository userRepository;

	private PartnerUserAssociationRepository partnerUserAssociationRepository;
	
	public AuthenticationServiceImpl(UserTokenRepository userTokenRepository,
			UserRepository userRepository,
			PartnerUserAssociationRepository partnerUserAssociationRepository) {
		this.userRepository = userRepository;
		this.userTokenRepository = userTokenRepository;
		this.partnerUserAssociationRepository = partnerUserAssociationRepository;
	}
	

	@Override
	public ApiResponse saveToken(UserToken userToken) {
		userToken.setCreatedDate(PartnerUtility.getCurrentDateTime());
		userToken.setStatus(1);
		userTokenRepository.save(userToken);
		return ApiResponse.builder().statusCode("000").statusMessage("Successfully saved").build();
	}

	/**
	 * Fetching User object and Partner object based on token
	 */
	@Override
	public List<Object> findByToken(String token) {
		log.info("AuthenticationServiceImpl : findByToken {}",token);
		UserToken userToken = userTokenRepository.findTopByOrderByTokenDesc(token);
		List<Object> lstDetails = new ArrayList<>();
		// If user token existed then pull User and Partner Object
		if(userToken!=null && userToken.getId()>0) {
			//User Details
			Optional<User> optUser = userRepository.findById(userToken.getUserId());
			// Adding User Object
			if(optUser.isPresent()) {
				lstDetails.add(optUser.get());
			}
			// Adding Partner Object
			Optional<PartnerUserAssociation> optPartnerUserAssociation =  partnerUserAssociationRepository.findByUserId(userToken.getUserId());
			if(optPartnerUserAssociation.isPresent()) {
				lstDetails.add(optPartnerUserAssociation.get());
			}
		}else {
			throw new InvalidTokenException(ResponseCodes.INVALID_TOKEN,"Unauthorized");
		}
		return lstDetails;
	}
}
