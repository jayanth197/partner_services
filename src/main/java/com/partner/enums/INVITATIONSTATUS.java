package com.partner.enums;

/**
 * 
 * @author Narasimhulu Chary
 * 
 *  Rule: At UI level, all status between OTC, BPI and Partner.  Before add any new status,
 *        check at UI level and add new status with appropriate status id.  Otherwise, it will
 *        cause problem while displaying in UI
 */

public enum INVITATIONSTATUS {

	NEW(1,"NEW"),
	ERROR(2,"ERROR"),
	PROCESSED(3,"PROCESSED"),
	TRANSMITTED(4,"TRANSMITTED"),
	ACTIVE(5,"ACTIVE"),
	INACTIVE(6,"INACTIVE"),
	INVITED(7,"INVITED"),
	REJECTED(8,"REJECTED"),
	SUCCESS(9,"SUCCESS"),
	INPROGRESS(10,"INPROGRESS");
	
	private final int statusId;
	private final String statusValue;
	
	private INVITATIONSTATUS(int statusId,String statusValue) {
		this.statusId = statusId;
		this.statusValue=statusValue;
	}
	
	public int getStatusId() {
		return this.statusId;
	}
	
	public String getStatusValue() {
		return this.statusValue;
	}
}
