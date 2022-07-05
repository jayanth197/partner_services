package com.partner.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerSearchCriteria implements Serializable{

	private static final long serialVersionUID = 7498100264143571916L;

	@NotNull(message = "Partner ID is mandatory with min size is 7")
	@Size(min = 7,message = "{txnsearch.partnerid.size}" )
	private String partnerId;
	private String keyword;
	
	/**
	 * Pagination properties
	 */
	private Integer pageNo; 
    private Integer pageSize;
	
}
