package com.partner.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.partner.entity.UserToken;

public interface UserTokenRepository extends CrudRepository<UserToken, Integer>{

	@Transactional
	//@Query(value="SELECT u FROM UserToken u WHERE u.token=:token ORDER BY 1 DESC")
	@Query(value="SELECT * FROM user_token u WHERE u.token=:token AND DATE_FORMAT(u.created_date,'%Y-%m-%d') = CURDATE() "
			+ " ORDER BY u.id DESC LIMIT 1 ",nativeQuery = true)
	UserToken findTopByOrderByTokenDesc(String token);
}
