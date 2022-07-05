package com.partner.enums;

public enum KEYTYPE {

	API_KEY("API_KEY"), PRIVATE_KEY("PRIVATE_KEY");

	private String value;

	private KEYTYPE(String keyType) {
		this.value = keyType;
	}

	public String getValue() {
		return value;
	}
}
