package com.partner.service;

import com.partner.entity.BpiKey;
import com.partner.model.ApiResponse;
import com.partner.model.KeyGenrator;

public interface KeyGeneratorService {
	
	public ApiResponse generateKeyAndSave(KeyGenrator keyGenerator, String keyType);
		
	public BpiKey getKeys(Integer partnerId);
	
	public ApiResponse validateApiKey(Integer partnerId, String apiKey);

}
