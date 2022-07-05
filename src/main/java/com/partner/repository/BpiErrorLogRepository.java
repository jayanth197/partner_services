package com.partner.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.partner.entity.ErrorLog;

@Repository
public interface BpiErrorLogRepository extends CrudRepository<ErrorLog, Integer>{

}
