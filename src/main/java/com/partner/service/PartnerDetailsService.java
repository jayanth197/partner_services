package com.partner.service;

import java.util.List;

import com.partner.entity.PartnerDetails;
import com.partner.model.ApiResponse;
import com.partner.model.PartnerSearchCriteria;
import com.partner.model.PartnerSearchResponse;

public interface PartnerDetailsService {
	ApiResponse savePartnerDetails(PartnerDetails partnerDetails);
	List<PartnerDetails> getAllPartners();
	PartnerDetails getPartnerById(int id);
	ApiResponse updatePartnerDetails(PartnerDetails partnerDetails);
	List<PartnerDetails> getFilterPartnerDetails(PartnerDetails partnerDetails);
	List<PartnerDetails> getPartnerByPartnerId(List lstPartnerAssociateIds);
	List<PartnerDetails> getPartnerByHostPartnerId(List lstPartnerAssociateIds);
	List<PartnerDetails> getPartnerByPartnerIdAndInviteePid(List lstPartnerAssociateIds,Integer initiatedPartnerId);
	List<PartnerDetails> getAllPartnersExceptPid(String partnerId);
	PartnerSearchResponse fetchAllPartners(PartnerSearchCriteria ptnrSearchCriteria);
	PartnerDetails findByPartnerId(Integer partnerId);
	List<PartnerDetails> partnerFreeTextSearch(String keyword);
}
