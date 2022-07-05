package com.partner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.partner.entity.PartnerOrgDetails;

public interface PartnerOrgDetailsRepository extends JpaRepository<PartnerOrgDetails, Integer>{
	@Query(value = "select * from tpod where partner_id=:partnerId",nativeQuery = true)
	Optional<PartnerOrgDetails> findByPartnerId(int partnerId);
	
	@Query(value = "select * from tpod where edi_isa_id=:isaId",nativeQuery = true)
	Optional<PartnerOrgDetails> findByEdiIsaId(String isaId);
	
	Optional<PartnerOrgDetails> findByExternalPartnerId(Integer epId);
	
	Optional<PartnerOrgDetails> findByCompanyNameAndWebsiteUrl(String companyName,String websiteURL);
	
	Optional<PartnerOrgDetails> findByCompanyNameAndEdiIsaId(String companyName,String ediIsaId);
	
	Optional<PartnerOrgDetails> findByCompanyName(String companyName);
}
