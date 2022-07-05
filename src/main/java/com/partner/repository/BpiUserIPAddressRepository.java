package com.partner.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.partner.entity.BpiUserIpAddress;
@Repository
public interface BpiUserIPAddressRepository extends CrudRepository<BpiUserIpAddress, Integer>{
	Optional<BpiUserIpAddress> findByUserIdAndIpAddress(int userId, String ipAddress);
}
