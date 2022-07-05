package com.partner.model;

import java.io.Serializable;

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
public class TradingAndExternalPartners implements Serializable{
	private static final long serialVersionUID = 644583686940946932L;
	private Integer partner;
	private String  type;
	private String  senderIsaId;
	private String  name;//It is organization name
	private String userId;
	private String userName;
}
