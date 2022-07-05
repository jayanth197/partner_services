package com.partner.repository;



import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.partner.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{

	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.isDetailsRequired = false WHERE u.userId = :uid")
	public int updateUserFlag(@Param("uid") int uid);
	
	Optional<User> findByUserName(@Param("userName") String userName);
	
	Optional<User> findByEmailAddress(@Param("emailAddress") String emailAddress);
	
	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.password= :pwd WHERE u.emailAddress = :email")
	public int updatePassword(@Param("email") String email,@Param("pwd") String pwd);
	
	Optional<List<User>> findByPartnerIdOrderByUserIdDesc(@Param("partnerId") int pid);
	
	Optional<User> findByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);
	
	Optional<User> findByUserNameAndIpAddress(String userName, String ipAddress);
	
	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.securityCode= :securityCode WHERE u.emailAddress = :email")
	public int updateSecurityCodeByEmail(String email,int securityCode);
	
	Optional<User> findByEmailAddressAndSecurityCode(String emailAddress, int twoFactorCode);
	/**
	 * Pagination
 * @param i 
	 */

		Page<User> findAllByPartnerId(@Param("partnerId") Integer partnerId,Pageable page);


	}

