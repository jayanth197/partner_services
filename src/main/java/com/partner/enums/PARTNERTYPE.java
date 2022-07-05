package com.partner.enums;

public enum PARTNERTYPE {
	TRADING_PARTNER("TP"),
	EXTERNAL_PARTNER("EP"),
	TRADING_APPLICATION("TA");
	
	private String value;
	
	private PARTNERTYPE(String partnerType) {
		this.value = partnerType;
	}
	
	public String getValue() {
		return value;
	}
}
