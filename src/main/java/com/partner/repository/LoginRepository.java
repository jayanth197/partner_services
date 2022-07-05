package com.partner.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.partner.entity.PartnerUserDetail;

public interface LoginRepository extends CrudRepository<PartnerUserDetail, Integer>{
	@Query(value="SELECT * FROM tpud WHERE user_email=:userName and password=:password",nativeQuery = true)
	public Optional<PartnerUserDetail> findByUserNameAndPassword(String userName, String password);
	
	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.password= :pwd WHERE u.emailAddress = :email")
	public int updatePassword(@Param("email") String email,@Param("pwd") String pwd);
	
}
