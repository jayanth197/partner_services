package com.partner.service;

import java.util.List;

import com.partner.entity.PartnerInvitation;
import com.partner.entity.PartnerUserDetail;
import com.partner.model.ApiResponse;

public interface PartnerUserDetailsService {
	String validateUser(String userEmail, String password);
	ApiResponse saveUser(PartnerUserDetail partnerUserDetail);
	List<PartnerUserDetail> findAll();
	ApiResponse sendPartnerInvitation(PartnerInvitation partnerInvitation);
}
