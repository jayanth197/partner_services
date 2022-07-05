package com.partner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.partner.entity.CustomerRequest;


@Repository
public interface CustomerRequestRepository extends CrudRepository<CustomerRequest,Integer>{

}
