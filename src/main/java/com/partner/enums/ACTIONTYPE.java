package com.partner.enums;

public enum ACTIONTYPE {

	CREATE("CREATE"),
	UPDATE("UPDATE"),
	DELETE("DELETE"),
	PASSWORD_CHANGE("PASSWORD-CHANGE");

	private final String actionType;
	
	private ACTIONTYPE(String actionType) {
		this.actionType = actionType;
	}
	
	public String getActiontType() {
		return this.actionType;
	}
}
