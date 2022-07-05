package com.partner.service.impl;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partner.config.PartnerConfigProperties;
import com.partner.entity.BpiKey;
import com.partner.enums.KEYTYPE;
import com.partner.model.ApiResponse;
import com.partner.model.KeyGenrator;
import com.partner.repository.BpiKeyRepository;
import com.partner.service.KeyGeneratorService;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

@Service
public class KeyGeneratorServiceImpl implements KeyGeneratorService {

	@Autowired
	BpiKeyRepository bpiKeyRepository;
	
	@Autowired
	PartnerConfigProperties partnerConfigProperties;

	@Override
	public ApiResponse generateKeyAndSave(KeyGenrator keyGenerator, String keyType) {
		BpiKey key = prepareBipKey(keyGenerator, keyType);
		bpiKeyRepository.save(key);
		String generatedKey = keyType.equalsIgnoreCase(KEYTYPE.PRIVATE_KEY.getValue()) ? key.getPrivateKey() : key.getApiKey();
		return ApiResponse.builder().statusCode(ResponseCodes.SUCCESS_CODE).statusMessage(partnerConfigProperties.keyGeneratedSuccess).
				key(generatedKey).build();
	}

	private BpiKey prepareBipKey(KeyGenrator keyGenerator, String keyType) {
		String generatedKey = generateSecureRandomKey();
		String privateKey = keyType.equalsIgnoreCase(KEYTYPE.PRIVATE_KEY.getValue()) ? generatedKey : null;
		String apiKey = keyType.equalsIgnoreCase(KEYTYPE.API_KEY.getValue()) ? generatedKey : null;
		BpiKey key = bpiKeyRepository.findByPartnerId(keyGenerator.getPartnerId());
		if (key != null) {
			if (keyType.equalsIgnoreCase(KEYTYPE.PRIVATE_KEY.getValue())) {
				key.setPrivateKey(privateKey);
			} else {
				key.setApiKey(apiKey);
			}
			key.setUpdatedDate(PartnerUtility.getCurrentDateTime());
			key.setUpdateddBy(keyGenerator.getUpdateddBy());
		} else {
			key = BpiKey.builder().apiKey(apiKey).privateKey(privateKey).partnerId(keyGenerator.getPartnerId())
					.createdDate(PartnerUtility.getCurrentDateTime()).createdBy(keyGenerator.getCreatedBy()).build();
		}
		return key;
	}

	@Override
	public BpiKey getKeys(Integer partnerId) {
		BpiKey key = bpiKeyRepository.findByPartnerId(partnerId);
		return key;
	}

	public ApiResponse validateApiKey(Integer partnerId, String apiKey) {
		BpiKey key = bpiKeyRepository.findByPartnerIdAndApiKey(partnerId, apiKey);
		if(key == null) {
			return ApiResponse.builder().statusCode(ResponseCodes.INVALID_API_KEY).statusMessage(partnerConfigProperties.apiKeyInValid).
					build();
		}else {
			return ApiResponse.builder().statusCode(ResponseCodes.SUCCESS_CODE).statusMessage(partnerConfigProperties.apiKeyValid).
					build();
		}
	}
	private String generateSecureRandomKey() {
		SecureRandom secRandom = new SecureRandom();
		byte[] result = new byte[32];
		secRandom.nextBytes(result);
		String randonKey = new BigInteger(1, result).toString(16);
		return UUID.nameUUIDFromBytes(randonKey.getBytes(StandardCharsets.UTF_8)).toString();

	}

}
