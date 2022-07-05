package com.partner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDashBoard {
	private long associatedPartners;
	private long external;
	private long externalInvites;
	//Sent partner requests
	private long internalInvites;
	//Received partner requests
	private long requests;
}
