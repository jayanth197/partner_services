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
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria implements Serializable{
	 
	private static final long serialVersionUID = 5443994610464550077L;
	
	@NotNull(message = "Partner ID is mandatory with min size is 7")
	@Size(min = 7,message = "{txnsearch.partnerid.size}" )
	private Integer partnerId;
	
	/**
	 * Pagination properties
	 */
	private Integer pageNo; 
    private Integer pageSize;
    
}




