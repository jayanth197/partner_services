package com.partner.model;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.partner.entity.PartnerAssociation;
import com.partner.entity.PartnerDetails;
import com.partner.entity.PartnerInvitation;
import com.partner.entity.TpExternal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PartnerSearchResponse {

	Pageable pageble;
	List<PartnerDetails> partnerDetails;
	List<PartnerAssociation> partnerAssociation;
	//List<TpApplication> tpApplication;
	List<TpExternal> tpExternal;
	List<PartnerInvitation> partnerInvitation;
}
