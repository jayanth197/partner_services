package com.partner.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.partner.config.TwilioConfigProperties;
import com.partner.entity.BpiConfig;
import com.partner.entity.EmailTemplate;
import com.partner.entity.ErrorLog;
import com.partner.enums.EMAILTEMPLATENAME;
import com.partner.model.ApiResponse;
import com.partner.model.Pageable;
import com.partner.repository.BpiConfigRepository;
import com.partner.repository.BpiErrorLogRepository;
import com.partner.repository.EmailTemplateRepository;
import com.partner.repository.UserRepository;
import com.partner.service.SendMail;
import com.partner.service.SmsService;
import com.twilio.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CommonUtil {

	private UserRepository userRepository;
	private TwilioConfigProperties twilioConfigProperties;
	private SmsService smsService;
	private BpiErrorLogRepository bpiErrorLogRepository;
	@Autowired
	private SendMail sendMail;
	private BpiConfigRepository bpiConfigRepository;
	private EmailTemplateRepository emailTemplateRepository;
	
	public CommonUtil(UserRepository userRepository,
			TwilioConfigProperties twilioConfigProperties,
			SmsService smsService,  
			BpiErrorLogRepository bpiErrorLogRepository,
			BpiConfigRepository bpiConfigRepository,
			EmailTemplateRepository emailTemplateRepository) {
		this.userRepository = userRepository;
		this.twilioConfigProperties = twilioConfigProperties;
		this.smsService = smsService;
		this.bpiErrorLogRepository = bpiErrorLogRepository;
		this.bpiConfigRepository = bpiConfigRepository;
		this.emailTemplateRepository = emailTemplateRepository;
	}
	
	public void saveErrorLog(Exception exception) {
		StackTraceElement[] stackTrace = exception.getStackTrace();
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		ErrorLog bpiErrorLog = ErrorLog.builder()
				.className(stackTrace[0].getClassName())
				.funName(stackTrace[0].getMethodName())
				.fileName(stackTrace[0].getFileName())
				.errorDescription(sw.toString())
				.exception(exception.toString())
				.createdDate(PartnerUtility.getCurrentDateTime())
				.createdBy("System")
				.build();
		log.info("Exception : {}",sw.toString());
		bpiErrorLogRepository.save(bpiErrorLog);
	}
	
	public String build2FASmsBody(String emailId,int securityCode, String body) {
		//Save security code
		int updateStatus = userRepository.updateSecurityCodeByEmail(emailId,securityCode);
		log.info("Updated security code in user table = "+updateStatus);
		if(updateStatus >0) {
			return body+" "+securityCode;
		}else {
			log.info("***** Check user_name and email_address in bpi_user table.  Both should be same. *****");
			return null;
		}
	}

	public ApiResponse handleTwilioSMS(String contactNumber, String body) {
		ApiResponse apiResponse=null;
		try{
			String confirmationCode = smsService.sendTwilioSMS(contactNumber,body);
			if(StringUtils.isNotEmpty(confirmationCode)) {
				apiResponse = ApiResponse.builder().statusCode("2FA").statusMessage("2FA SENT SUCCESSFULLY").build();
			}
		} catch (ApiException exception) {
			log.info("Unable to send SMS to : "+contactNumber+";  Error Code : "+exception.getCode()+"; Error Message: "+exception.getMessage());
			saveErrorLog(exception);
			apiResponse = ApiResponse.builder().statusCode("2FA-Failure").statusMessage(exception.getMessage()).build();
		}
		if(apiResponse.getStatusMessage().contains("is not a valid phone number")) {
			apiResponse = ApiResponse.builder().statusCode("2FA").statusMessage("2FA SENT SUCCESSFULLY").build();
		}
		return apiResponse;
	}

	public ApiResponse send2FAAuthCodeEmail(EmailTemplate emailTemplate, String emailAddress,String securityCode)
	{
		ApiResponse apiResponse=null;
		String body = emailTemplate.getTemplateBody();
		if(StringUtils.isNoneEmpty(body) && emailTemplate!=null && StringUtils.isNoneEmpty(emailAddress) && StringUtils.isNoneEmpty(securityCode)) {
			body = body.replace("@@SecurityCode", securityCode);
			sendMail.sendEmail(emailAddress, emailTemplate.getTemplateSubject(), body, EMAILTEMPLATENAME.TWO_FACTOR_AUTHENTICATION_TEMPLATE.getEmailTempateName());
		}
		return apiResponse;
	}

	public BpiConfig findBpiConfigByKey(String key) {
		Optional<BpiConfig> optBpiConfig = bpiConfigRepository.findByConfigKey(key);
		if(optBpiConfig.isPresent()) {
			return optBpiConfig.get();
		}else {
			return null;
		}
	}
	
	public String fetchIPAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");  
		if (ipAddress == null) {  
			ipAddress = request.getRemoteAddr();  
		}
		log.info("CommonBpiServiceHandler : FetchIPAddress - Request received from IP Address : "+ipAddress);
		return ipAddress;
	}
	
	public Pageable buildPage(Integer pageNo,Integer pageSize,Integer totalPage) {
		return Pageable.builder()
				.pageNo(pageNo)
				.pageSize(pageSize)
				.totalPages(totalPage)
				.build();
	}
	
	/**
	 * 
	 * @param sourceDate --> updatedDate from bpi_user table 
	 * @param expiryMinutes --> pulling it from bpi_config table and it is completely configurable
	 * @return --> Validation logic --> Take updatedDate + add expiry minutes then compare with current date and time
	 * @throws ParseException
	 */
	public boolean validateExpiryMinutes(String sourceDate,int expiryMinutes)
	{
		try {
			Date source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sourceDate);
			source = DateUtils.addMinutes(source, expiryMinutes);
			Date currentDate = new Date();
			return currentDate.before(source) ? Boolean.TRUE : Boolean.FALSE;
		} catch (ParseException exception) {
			log.info("Validate Expiry Minutes : Exception  "+exception);
			return Boolean.FALSE;
		}
	}
}
