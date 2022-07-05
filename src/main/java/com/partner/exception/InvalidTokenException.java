package com.partner.exception;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException{

	private static final long serialVersionUID = 3124651921030063459L;
	private final String code;
	private final String errorMessage;
	
	public InvalidTokenException(final String code,final String errorMessage) {
		this.code = code;
		this.errorMessage = errorMessage;
	}
	
}
