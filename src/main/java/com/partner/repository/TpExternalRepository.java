package com.partner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.partner.entity.TpExternal;

@Repository
public interface TpExternalRepository extends CrudRepository<TpExternal, Integer>{
	Optional<List<TpExternal>> findByOwnerPartnerID(@Param("partnerId") Integer partnerId);
	long countByOwnerPartnerID(Integer partnerId);
/**
 * Pagination
 */

	Page<TpExternal> findByOwnerPartnerID(@Param("partnerId") Integer partnerId,Pageable page);
}
