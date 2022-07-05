package com.partner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pageable {
	private Integer pageNo; 
    private Integer pageSize;
    private Integer totalPages;
    
}
