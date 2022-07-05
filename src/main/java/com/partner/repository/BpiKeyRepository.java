package com.partner.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.partner.entity.BpiKey;

@Repository
public interface BpiKeyRepository  extends CrudRepository<BpiKey,Integer>{
	
	BpiKey findByPartnerId(Integer partnerId);
	
	BpiKey findByPartnerIdAndApiKey(Integer partnerId, String apiKey);

}
