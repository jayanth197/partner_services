package com.partner.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.partner.model.ApiResponse;
import com.partner.util.CommonUtil;
import com.partner.util.PartnerConstants;
import com.partner.util.ResponseCodes;

@ControllerAdvice
public class PartnerGlobalExceptionHadler extends ResponseEntityExceptionHandler{

	private CommonUtil commonUtil;
	
	public PartnerGlobalExceptionHadler(CommonUtil commonUtil) {
		this.commonUtil = commonUtil;
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException invalidArgError,HttpHeaders httpHeaders,HttpStatus httpStatus,WebRequest request){
		Map<String, String> errors = new HashMap<>();
		invalidArgError.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ApiResponse> exceptionHandler(Exception cintapApiException){
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatusCode(ResponseCodes.ERROR_CODE);
		apiResponse.setStatusMessage(PartnerConstants.INTERNAL_TECHNICAL_ERROR);
		commonUtil.saveErrorLog(cintapApiException);
		return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = InvalidCredentialsException.class)
	public ResponseEntity<ApiResponse> exceptionHandler(InvalidCredentialsException invalidCredentialsException){
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatusCode(invalidCredentialsException.getCode());
		apiResponse.setStatusMessage(invalidCredentialsException.getErrorMessage());
		commonUtil.saveErrorLog(invalidCredentialsException);
		return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = PartnerNotFoundException.class)
	public ResponseEntity<ApiResponse> partnerExceptionHandler(PartnerNotFoundException partnerNotFoundException){
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatusCode(partnerNotFoundException.getCode());
		apiResponse.setStatusMessage(partnerNotFoundException.getErrorMessage());
		commonUtil.saveErrorLog(partnerNotFoundException);
		return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = InvalidTokenException.class)
	public ResponseEntity<ApiResponse> invalidTokenExceptionHandler(InvalidTokenException invalidTokenException){
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatusCode(invalidTokenException.getCode());
		apiResponse.setStatusMessage(invalidTokenException.getErrorMessage());
		commonUtil.saveErrorLog(invalidTokenException);
		return new ResponseEntity<>(apiResponse,HttpStatus.UNAUTHORIZED);
	}
}
