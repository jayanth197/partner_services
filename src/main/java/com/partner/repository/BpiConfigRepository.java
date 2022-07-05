package com.partner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.partner.entity.BpiConfig;

@Repository
public interface BpiConfigRepository extends CrudRepository<BpiConfig, Integer>{
	Optional<BpiConfig> findByConfigKey(String proertyKey);
	Optional<List<BpiConfig>> findByStatus(int status);
}
