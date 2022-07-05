package com.partner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import com.partner.entity.PartnerDetails;

@Repository
public interface PartnerDetailsRepository extends JpaRepository<PartnerDetails, Integer>,QueryByExampleExecutor<PartnerDetails>{
	//@Transactional
	//@Query("SELECT p FROM PartnerDetails p where p.createdBy=:createdBy")
	//PartnerDetails findByCreatedBy(int createdBy);
	@Query("SELECT p FROM PartnerDetails p where p.createdBy=:createdBy")
	Optional<PartnerDetails> findCreatedByUser(int createdBy);

	@Query("SELECT p FROM PartnerDetails p where p.partnerId=:partnerId")
	Optional<PartnerDetails> findByPartnerId(int partnerId);

	//Fetch My Partner invitations - Sent Partner requests
	@Query(value="select * from tpd p join tplink a on p.tpid = a.host_pid where a.host_pid=:partnerId and a.status_id=:statusId",nativeQuery = true)
	Optional<List<PartnerDetails>> findMyPartnerInvitations(int partnerId,int statusId);

	//Fetch My Partner invitations - Sent Partner requests
	@Query(value="select count(*) from tpd p join tplink a on p.tpid = a.host_pid where a.host_pid=:partnerId and a.status_id=:statusId",nativeQuery = true)
	long findMyPartnerInvitationsCount(int partnerId,int statusId);

	//Fetch my invited partners - Recieved partner requests
	@Query(value="select * from tpd p join tplink a on p.tpid = a.invitee_pid where a.invitee_pid=:partnerId and a.status_id=:statusId",nativeQuery = true)
	Optional<List<PartnerDetails>> findReceivedPartnerInvitations(int partnerId,int statusId);

	@Query(value="select count(*) from tpd p join tplink a on p.tpid = a.invitee_pid where a.invitee_pid=:partnerId and a.status_id=:statusId",nativeQuery = true)
	long findReceivedPartnerInvitationsCount(int partnerId,int statusId);

	//Fetch my associated partners
	@Query(value="select * from tpd p join tplink a on p.tpid = a.host_pid where p.tpid=:partnerId and a.status_id=:statusId",nativeQuery = true)
	Optional<List<PartnerDetails>> findPartnerAssociation(int partnerId,int statusId);

	//Fetch all partners except login partner
	@Query(value="SELECT * FROM tpd p where tpid not in(:partnerId)",nativeQuery = true)
	List<PartnerDetails> findByDetailsExceptPid(@Param("partnerId") String partnerId);

	Optional<PartnerDetails> findByAddressLine1AndStateNameAndCityName(String address,String state,String city);

	/**
	 * New methods with Pagination
	 */
	//Fetch my associated partners
	@Query(value="select * from tpd p join tplink a on p.tpid = a.host_pid where p.tpid=:partnerId and a.status_id=:statusId",nativeQuery = true)
	Page<PartnerDetails> findPartnerAssociationWithPatination(int partnerId,int statusId,Pageable page);

	
	Page<PartnerDetails> findAll(Pageable page);

	@Query(value="SELECT * FROM tpd u join tpod t on u.tpid = t.partner_id where u.tpid like %:keyword% or u.first_name like %:keyword% or u.middle_name LIKE %:keyword% or u.last_name like %:keyword% or u.primary_email like %:keyword% or u.primary_telephone like %:keyword% or t.company_name like %:keyword% or t.website_url like %:keyword% or t.edi_isa_id like %:keyword% and u.tpid not in (select p.tpid from tpd p join tplink l on p.tpid = l.host_pid where l.host_pid=:partnerId and l.status_id IN (5,7,8))",nativeQuery = true)
	Page<PartnerDetails> findByKeyword(String keyword, String partnerId, Pageable paging);
	
	@Query(value="SELECT * FROM tpd u WHERE u.tpid like %:keyword% or u.first_name like %:keyword% or u.middle_name LIKE %:keyword% or u.last_name like %:keyword% or u.primary_email like %:keyword% or u.primary_telephone like %:keyword%",nativeQuery = true)
	List<PartnerDetails> findPartnerByKeyword(@Param("keyword") String keyword);

}
