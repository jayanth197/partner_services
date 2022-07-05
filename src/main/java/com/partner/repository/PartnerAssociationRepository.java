package com.partner.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.partner.entity.PartnerAssociation;

public interface PartnerAssociationRepository extends JpaRepository<PartnerAssociation, Long>{
	//To make partner Active and InActive
	@Transactional
	@Modifying
	@Query("UPDATE PartnerAssociation p SET p.status= :status WHERE p.sourcePartnerId = :sourcePartnerId and p.associatedPartnerId = :associatedPartnerId and p.status= :previousStatus")
	public int updateStatus(@Param("status") int status,@Param("sourcePartnerId") int sourcePartnerId,@Param("associatedPartnerId") int associatedPartnerId,
			@Param("previousStatus") int previousStatus);

	Optional<List<PartnerAssociation>> findByAssociatedPartnerIdAndStatus(int associatedPartnerId,int status);

	Optional<PartnerAssociation> findBySourcePartnerIdAndAssociatedPartnerId(int sourcePartnerId,int associatedPartnerId);

	//Update Partner Invitation Status
	@Transactional
	@Modifying
	@Query(value="UPDATE tplink p SET p.status_id= :status,p.reject_reason=:reason WHERE p.link_id = :associationId",nativeQuery = true)
	public int updateInvitationStatus(@Param("status") int status,@Param("reason") String reason,@Param("associationId") int associationId);

	@Transactional
	@Modifying
	@Query(value="UPDATE tplink p SET p.status_id= :status,p.reject_reason=:reason WHERE p.host_pid = :sourcePid and p.invitee_pid=:associationId",nativeQuery = true)
	public int updateInvitationStatusByPid(@Param("status") int status,@Param("reason") String reason,@Param("sourcePid") int sourcePid,@Param("associationId") int associationId);

	//Associated partners and Sent partner requests
	@Query(value="select * from tplink where (host_pid=:partnerId or invitee_pid=:partnerId) and status_id=:statusId",nativeQuery = true)
	Optional<List<PartnerAssociation>> findPartnerByPartnerId(@Param("partnerId") int partnerId,@Param("statusId") int statusId);

	//Received partners requests
	@Query(value="select * from tplink where invitee_pid=:partnerId and status_id=:statusId",nativeQuery = true)
	Optional<List<PartnerAssociation>> findMyReceivedPartnerRequests(@Param("partnerId") int partnerId,@Param("statusId") int statusId);

	//Received partners requests
	@Query(value="select * from tplink where (host_pid=:hostPartnerId and invitee_pid=:inviteePartnerId) or (host_pid=:inviteePartnerId and invitee_pid=:hostPartnerId)",nativeQuery = true)
	Optional<PartnerAssociation> checkInvitationStatus(@Param("hostPartnerId") int hostPartnerId,@Param("inviteePartnerId") int inviteePartnerId);

	@Query(value="select count(*) from tplink where (host_pid=:tpId or invitee_pid=:tpId) and status_id=:status",nativeQuery = true)
	long countByPartnerIdAndStatus(@Param("tpId") Integer tpId,@Param("status") int status);

	/**
	 * With Pagination
	 */
	//Associated partners and Sent partner requests
	@Query(value="select * from tplink where (host_pid=:partnerId or invitee_pid=:partnerId) and status_id=:statusId",nativeQuery = true)
	Page<PartnerAssociation> findPartnerByPtnrId(@Param("partnerId") int partnerId,@Param("statusId") int statusId,Pageable page);
	
	@Query(value="select * from tplink where (host_pid=:partnerId or invitee_pid=:partnerId) and status_id=:statusId",nativeQuery = true)
	Page<PartnerAssociation> findMyReceivedPartnerRequests(@Param("partnerId") int partnerId,@Param("statusId") int statusId,Pageable page);
	
}
