package com.partner.service;

import com.partner.entity.PartnerUserDetail;

public interface LoginService {
	Object findByUser(PartnerUserDetail partnerUserDetail,String ipAddress,String source);
	boolean validateIPAddress(int userId,String ipAddress);
}
