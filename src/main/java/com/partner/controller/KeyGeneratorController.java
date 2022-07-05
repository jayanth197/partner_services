package com.partner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partner.entity.BpiKey;
import com.partner.enums.KEYTYPE;
import com.partner.model.ApiResponse;
import com.partner.model.KeyGenrator;
import com.partner.service.KeyGeneratorService;
import com.partner.util.PartnerUtility;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
public class KeyGeneratorController {

	@Autowired
	KeyGeneratorService keyGeneratorService;

	@PostMapping(value = "/generateApiKey")
	public ResponseEntity<ApiResponse> generateApiKey(@RequestBody KeyGenrator keyGenerator) {
		log.info("KeyGeneratorController | generateApiKey - Request : "
				+ PartnerUtility.convertObjectToJson(keyGenerator));
		ApiResponse apiResponse = keyGeneratorService.generateKeyAndSave(keyGenerator, KEYTYPE.API_KEY.getValue());

		log.info("KeyGeneratorController | generateApiKey : Response - "
				+ PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/generatePrivateKey")
	public ResponseEntity<ApiResponse> generatePrivateKey(@RequestBody KeyGenrator keyGenerator) {
		log.info("KeyGeneratorController | generatePrivateKey - Request : "
				+ PartnerUtility.convertObjectToJson(keyGenerator));
		ApiResponse apiResponse = keyGeneratorService.generateKeyAndSave(keyGenerator, KEYTYPE.PRIVATE_KEY.getValue());

		log.info("KeyGeneratorController | generatePrivateKey : Response - "
				+ PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping("/getKeys/{partnerId}")
	public ResponseEntity<BpiKey> getKeys(@PathVariable final int partnerId) {
		log.info("KeyGeneratorController | getKeys - partnerId : " + partnerId);
		BpiKey key = null;
		key = keyGeneratorService.getKeys(partnerId);
		log.info("KeyGeneratorController | getKeys -  : " + key.toString());
		return new ResponseEntity<>(key, HttpStatus.OK);
	}
	
	@GetMapping("/validateKey/{partnerId}/{key}")
	public ResponseEntity<ApiResponse> validateKey(@PathVariable final int partnerId,@PathVariable final String key) {
		log.info("KeyGeneratorController | validateKey - Request partnerId {} , apikey {} : ",
				 partnerId,key );
		ApiResponse apiResponse = keyGeneratorService.validateApiKey(partnerId, key);

		log.info("KeyGeneratorController | validateKey : Response - "
				+ PartnerUtility.convertObjectToJson(apiResponse));
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}