package com.partner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.partner.entity.PartnerInvitation;

@Repository
public interface PartnerInvitationRepository extends JpaRepository<PartnerInvitation, Long>{
	Optional<List<PartnerInvitation>> findBySenderPartnerId(@Param("partnerId") String partnerId);
	
	/**
	 * Pagination
	 * @param  
	 */

		Page<PartnerInvitation> findBySenderPartnerId(String partnerId, Pageable page);
	}


