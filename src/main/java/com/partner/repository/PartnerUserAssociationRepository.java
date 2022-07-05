package com.partner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.partner.entity.PartnerUserAssociation;

public interface PartnerUserAssociationRepository extends JpaRepository<PartnerUserAssociation, Long>{
	Optional<PartnerUserAssociation> findByUserId(int userId);
}
