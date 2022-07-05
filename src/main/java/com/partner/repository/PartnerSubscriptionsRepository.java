package com.partner.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.partner.entity.PartnerSubscriptions;

@Repository
public interface PartnerSubscriptionsRepository extends CrudRepository<PartnerSubscriptions, Integer>{

}
