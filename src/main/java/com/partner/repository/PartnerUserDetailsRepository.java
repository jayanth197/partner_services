package com.partner.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.partner.entity.PartnerUserDetail;

@Repository
public interface PartnerUserDetailsRepository extends CrudRepository<PartnerUserDetail, Integer>{

	@Query("SELECT p FROM PartnerUserDetail p WHERE userEmail= :userEmail AND password=:password")
	Optional<PartnerUserDetail> validateUserAndGetPartnerISAId(@Param("userEmail")String userEmail, @Param("password")String password);

	@Transactional
	@Modifying
	@Query("UPDATE PartnerUserDetail u SET u.isDetailsRequired = false WHERE u.partnerUserId = :uid")
	int updateUserFlag(@Param("uid") int uid);
	
	Optional<PartnerUserDetail> findByEmailAddress(@Param("emailAddress")String emailAddress);
	
	/*@Transactional
	@Modifying
	@Query("UPDATE PartnerUserDetail u SET u.password= :pwd WHERE u.emailAddress = :email")
	public int updatePassword(@Param("email") String email,@Param("pwd") String pwd);*/

	@Transactional
	@Modifying
	@Query(value="UPDATE tpud SET password= :newPassword WHERE password = :oldPassword and user_email=:email",nativeQuery = true)
	public int updatePassword(@Param("newPassword") String newPassword,@Param("oldPassword") String oldPassword,@Param("email") String email);
	
	@Query(value="select * from tplink where (host_pid=:partnerId or invitee_pid=:partnerId) and status_id=:statusId",nativeQuery = true)
	Page<PartnerUserDetail> findPartnerByPtnrId(@Param("partnerId") int partnerId,@Param("statusId") int statusId,Pageable page);


}
