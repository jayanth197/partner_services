package com.partner.service;

import com.partner.entity.PartnerAssociation;
import com.partner.model.ApiResponse;

public interface PartnerAssociationService {
	ApiResponse savePartner(PartnerAssociation partnerAssociation);
	ApiResponse updateStatus(PartnerAssociation partnerAssociation);
	ApiResponse updateInvitationStatus(PartnerAssociation partnerAssociation);
	PartnerAssociation getPartnerAssociationStatus(PartnerAssociation partnerAssociation);
}
