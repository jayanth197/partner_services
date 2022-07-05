package com.partner.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.partner.config.PartnerConfigProperties;
import com.partner.entity.AuditLog;
import com.partner.entity.PartnerAssociation;
import com.partner.entity.PartnerDetails;
import com.partner.entity.PartnerOrgDetails;
import com.partner.entity.PartnerUserDetail;
import com.partner.entity.User;
import com.partner.enums.ACTIONTYPE;
import com.partner.enums.INVITATIONSTATUS;
import com.partner.enums.PARTNERTYPE;
import com.partner.exception.PartnerNotFoundException;
import com.partner.model.ApiResponse;
import com.partner.model.PartnerSearchCriteria;
import com.partner.model.PartnerSearchResponse;
import com.partner.repository.PartnerDetailsRepository;
import com.partner.repository.PartnerOrgDetailsRepository;
import com.partner.repository.PartnerUserDetailsRepository;
import com.partner.repository.UserRepository;
import com.partner.service.PartnerDetailsService;
import com.partner.util.CommonUtil;
import com.partner.util.ImageUploadUtil;
import com.partner.util.PartnerConstants;
import com.partner.util.PartnerUtility;
import com.partner.util.ResponseCodes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PartnerDetailsServiceImpl implements PartnerDetailsService{

	private PartnerDetailsRepository partnerDetailsRepository;
	private PartnerUserDetailsRepository partnerUserDetailsRepository;
	private UserRepository userRepository;
	private PartnerOrgDetailsRepository partnerOrgDetailsRepository;
	private PartnerConfigProperties partnerConfigProperties;
	private ImageUploadUtil imageUploadUtil;
	private CommonUtil commonUtil;
	private AuditLogServiceImpl auditLogServiceImpl;

	public PartnerDetailsServiceImpl(PartnerDetailsRepository partnerDetailsRepository,
			PartnerUserDetailsRepository partnerUserDetailsRepository,
			UserRepository userRepository,
			PartnerOrgDetailsRepository partnerOrgDetailsRepository,
			PartnerConfigProperties partnerConfigProperties,
			ImageUploadUtil imageUploadUtil,CommonUtil commonUtil,
			AuditLogServiceImpl auditLogServiceImpl) {
		this.partnerDetailsRepository = partnerDetailsRepository;
		this.partnerUserDetailsRepository = partnerUserDetailsRepository;
		this.userRepository = userRepository;
		this.partnerOrgDetailsRepository = partnerOrgDetailsRepository;
		this.partnerConfigProperties = partnerConfigProperties;
		this.imageUploadUtil = imageUploadUtil;
		this.commonUtil = commonUtil;
		this.auditLogServiceImpl = auditLogServiceImpl;
	}

	// Save Partner Details
	@Override
	public ApiResponse savePartnerDetails(PartnerDetails partnerDetails) {
		log.info("PartnerDetailsServiceImpl :: savePartnerDetails - "+partnerDetails.toString());
		ApiResponse response=null;
		try {
			boolean isParnterExists = validateDuplicatePartner(partnerDetails);
			if(isParnterExists) {
				response = new ApiResponse();
				response.setStatusCode(ResponseCodes.DUPLICATE_PARTNER);
				response.setStatusMessage(partnerConfigProperties.duplicatePartner);
			}else {
				PartnerDetails pDetails = new PartnerDetails(); 
				pDetails = partnerDetails;	
				// Adding partner org details
				pDetails.addPartnerOrgDetails(pDetails.getPartnerOrgDetails());
				pDetails.getPartnerOrgDetails().setPartnerType(PARTNERTYPE.TRADING_PARTNER.getValue());
				// Adding partner user details
				pDetails.addPartnerUserAssociation(pDetails.getPartnerUserAssociation());
				pDetails.setCreatedDate(PartnerUtility.getCurrentDateTime());
				pDetails.setStatusId(INVITATIONSTATUS.ACTIVE.getStatusId());
				log.info("PartnerDetailsServiceImpl :: savePartnerDetails - Final Object before save "+PartnerUtility.convertObjectToJson(partnerDetails));
				String imageName=null;
				String imgArray[];
				if(!StringUtils.isEmpty(pDetails.getProfilePicData())) {
					/**
					 * uploadProfileImage(p1-image data;p2-user first name;p3-profile image path;p4-partnerId;p5-PROFILE to identify source folder)
					 */
					imgArray = pDetails.getProfilePicData().split(",");

					imageName = imageUploadUtil.uploadProfileImag(imgArray.length==2?imgArray[1]:imgArray[0], pDetails.getProfilePicName()
							,PartnerConstants.PROFILE_IMG_PATH,pDetails.getPartnerUserAssociation().getUserId(),"PROFILE");
					pDetails.setProfilePicName(imageName);
				}
				PartnerDetails partnerDetailsObject = partnerDetailsRepository.save(pDetails);
				partnerDetails.setPartnerId(partnerDetailsObject.getPartnerId());
				response = new ApiResponse();
				response.setStatusCode(ResponseCodes.SUCCESS);
				response.setStatusMessage(partnerConfigProperties.onboardingSuccessMessage);
				response.setResult(partnerDetailsObject);
				//Update user flag in partner user table
				partnerUserDetailsRepository.updateUserFlag(Integer.parseInt(partnerDetails.getCreatedBy()));
				//Add a record in bpi_user table for login purpose
				// Pull user details based on email address;
				//PartnerUserDetailsRepository
				PartnerUserDetail partnerUserDetail=null;
				Optional<PartnerUserDetail> optUserDetails = partnerUserDetailsRepository.findByEmailAddress(partnerDetails.getPartnerEmail());
				if(optUserDetails.isPresent()) {
					partnerUserDetail = optUserDetails.get();
				}

				//Fetch user record
				Optional<User> optUser = userRepository.findByEmailAddress(partnerDetails.getPartnerEmail());
				User user =null;
				//If user exists in bpi_user table then update required fields if not create new user  
				if(optUser.isPresent()) {
					user = optUser.get();
					user.setIpAddress(partnerDetails.getIpAddress());
					user.setOrganization(partnerDetails.getPartnerOrgDetails().getCompanyName());
					user.setPartnerId(partnerDetails.getPartnerId());
				}else {
					user = User.builder()
							.organization(partnerDetails.getPartnerOrgDetails().getCompanyName())
							.contactNumber(partnerDetails.getPrimaryTelephone())
							.isDetailsRequired(false)
							.emailAddress(partnerDetails.getPartnerEmail())
							.firstName(partnerDetails.getFirstName())
							.lastName(partnerDetails.getLastName())
							.middleInitial(partnerDetails.getMiddleName())
							.partnerId(partnerDetails.getPartnerId())
							.password(partnerUserDetail!=null?partnerUserDetail.getPassword():"")
							.permission("Admin")
							.type("Read-Write")
							.partnerUserId(partnerUserDetail!=null?partnerUserDetail.getPartnerUserId():0)
							.userName(partnerDetails.getPartnerEmail())
							.createdDate(PartnerUtility.getCurrentDateTime())
							.createdBy(partnerDetails.getFirstName()+" "+partnerDetails.getLastName())
							.ipAddress(partnerDetails.getIpAddress())							
							.build();
				}
				user.setDetailsRequired(false);
				userRepository.save(user);
				log.info("PartnerDetailsServiceImpl :: savePartnerDetails - Updated status");
				
				AuditLog auditLog = AuditLog.builder()
						.actionType(ACTIONTYPE.CREATE.getActiontType())
						.action(partnerDetails.getAction())
						.createdDate(PartnerUtility.getCurrentDateTime())
						.createdBy(partnerDetails.getCreatedBy())
						.partnerId(partnerDetails.getPartnerId())
						.build();
				
				auditLogServiceImpl.saveAuditLog(auditLog);
			}
		}catch(DataIntegrityViolationException exception) {
			log.info("PartnerDetailsServiceImpl :: ", exception);
			return ApiResponse.builder().statusCode(ResponseCodes.DUPLICATE_EMAIL_ADDRESS).statusMessage(partnerConfigProperties.emailDuplicateMessage).build();
		}
		return response;
	}

	public boolean validateDuplicatePartner(PartnerDetails partnerDetails) {
		boolean isExisted =false;
		if(partnerDetails!=null) {
			//Validation1
			Optional<PartnerOrgDetails> optPartnerOrg = partnerOrgDetailsRepository.findByCompanyName(partnerDetails.getPartnerOrgDetails().getCompanyName());
			if(optPartnerOrg.isPresent()) {
				isExisted=true;
			}else {
				Optional<PartnerOrgDetails> optPartnerOrgDetails = partnerOrgDetailsRepository.findByCompanyNameAndWebsiteUrl(partnerDetails.getPartnerOrgDetails().getCompanyName(), 
						partnerDetails.getPartnerOrgDetails().getWebsiteUrl());
				if(optPartnerOrgDetails.isPresent()) {
					isExisted=true;
				}else {
					//Validation2
					Optional<PartnerOrgDetails> optPartnerOrgDetails1 = partnerOrgDetailsRepository.findByCompanyNameAndEdiIsaId(partnerDetails.getPartnerOrgDetails().getCompanyName(), 
							partnerDetails.getPartnerOrgDetails().getEdiIsaId());
					if(optPartnerOrgDetails1.isPresent()) {
						isExisted=true;
					}else {
						//Validation3
						//Optional<PartnerDetails> optPartnerDetails = partnerDetailsRepository.findByAddressLine1AndStateNameAndCityName(partnerDetails.getAddressLine1(), 
						//		partnerDetails.getCityName(), partnerDetails.getStateName());
						//if(optPartnerDetails.isPresent()) {
							Optional<PartnerOrgDetails> optPartnerOrgDetails2 = partnerOrgDetailsRepository.findByCompanyName(partnerDetails.getPartnerOrgDetails().getCompanyName());
							if(optPartnerOrgDetails2.isPresent() && optPartnerOrgDetails2.get().getCompanyName().equalsIgnoreCase(partnerDetails.getPartnerOrgDetails().getCompanyName())) {
								isExisted=true;
							}
						//}
					}
				}
			}
		}
		return isExisted;
	}

	@Override
	public List<PartnerDetails> getAllPartners() {
		Iterable<PartnerDetails> it = partnerDetailsRepository.findAll();
		List<PartnerDetails> lstPartnerDetails = new ArrayList<>();
		it.forEach(e->lstPartnerDetails.add(e));
		return lstPartnerDetails;
	}

	@Override
	public List<PartnerDetails> getAllPartnersExceptPid(String partnerId) {
		Iterable<PartnerDetails> it = partnerDetailsRepository.findByDetailsExceptPid(partnerId);
		List<PartnerDetails> lstPartnerDetails = new ArrayList<>();
		it.forEach(e->lstPartnerDetails.add(e));
		return lstPartnerDetails;
	}

	//Fetch association partner details
	@Override 
	public List<PartnerDetails> getPartnerByPartnerId(List lstPartnerAssociateIds) {
		List<PartnerDetails> lstPartnerDetails=new ArrayList<>();
		Iterator<PartnerAssociation> piterator = lstPartnerAssociateIds.iterator();
		while(piterator.hasNext()) {
			PartnerAssociation pAssociation = (PartnerAssociation)piterator.next();
			Optional<PartnerDetails> pdetails = partnerDetailsRepository.findByPartnerId(pAssociation.getAssociatedPartnerId());
			if(pdetails.isPresent()) {
				PartnerDetails pd = pdetails.get();
				lstPartnerDetails.add(pd);
			}
		}
		return lstPartnerDetails; 
	}

	//Fetch association partner details
	@Override 
	public List<PartnerDetails> getPartnerByHostPartnerId(List lstPartnerAssociateIds) {
		List<PartnerDetails> lstPartnerDetails=new ArrayList<>();
		Iterator<PartnerAssociation> piterator = lstPartnerAssociateIds.iterator();
		while(piterator.hasNext()) {
			PartnerAssociation pAssociation = (PartnerAssociation)piterator.next();
			Optional<PartnerDetails> pdetails = partnerDetailsRepository.findByPartnerId(pAssociation.getSourcePartnerId());
			if(pdetails.isPresent()) {
				PartnerDetails pd = pdetails.get();
				lstPartnerDetails.add(pd);
			}
		}
		return lstPartnerDetails; 
	}

	@Override
	public PartnerDetails getPartnerById(int partnerId) {
		Optional<PartnerDetails> optionalPartnerDetails = partnerDetailsRepository.findByPartnerId(partnerId);
		PartnerDetails partnerDetails = new PartnerDetails();
		if(optionalPartnerDetails.isPresent()) {
			partnerDetails = optionalPartnerDetails.get();
		}
		return partnerDetails;
	}

	@Override
	public ApiResponse updatePartnerDetails(PartnerDetails partnerDetails) {
		ApiResponse response = null;
		try {
			Optional<PartnerDetails> pDetails = partnerDetailsRepository.findById(partnerDetails.getPartnerId());
			if(pDetails.isPresent()) {
				PartnerDetails partnerDetailsObj = pDetails.get();
				if(!StringUtils.isEmpty(partnerDetails.getFirstName())) {
					partnerDetailsObj.setFirstName(partnerDetails.getFirstName());
				}
				if(!StringUtils.isEmpty(partnerDetails.getMiddleName())) {
					partnerDetailsObj.setMiddleName(partnerDetails.getMiddleName());
				}
				if(!StringUtils.isEmpty(partnerDetails.getLastName())) {
					partnerDetailsObj.setLastName(partnerDetails.getLastName());
				}
				if(!StringUtils.isEmpty(partnerDetails.getPartnerEmail())) {
					partnerDetailsObj.setPartnerEmail(partnerDetails.getPartnerEmail());
				}
				if(!StringUtils.isEmpty(partnerDetails.getPrimaryTelephone())) {
					partnerDetailsObj.setPrimaryTelephone(partnerDetails.getPrimaryTelephone());
				}
				if(!StringUtils.isEmpty(partnerDetails.getAddressLine1())) {
					partnerDetailsObj.setAddressLine1(partnerDetails.getAddressLine1());
				}
				if(!StringUtils.isEmpty(partnerDetails.getAddressLine2())) {
					partnerDetailsObj.setAddressLine2(partnerDetails.getAddressLine2());
				}
				if(partnerDetails.getZipCode()>0) {
					partnerDetailsObj.setZipCode(partnerDetails.getZipCode());
				}
				if(!StringUtils.isEmpty(partnerDetails.getCityName())) {
					partnerDetailsObj.setCityName(partnerDetails.getCityName());
				}
				if(!StringUtils.isEmpty(partnerDetails.getStateName())) {
					partnerDetailsObj.setStateName(partnerDetails.getStateName());
				}
				if(!StringUtils.isEmpty(partnerDetails.getProfilePicName())) {
					partnerDetailsObj.setProfilePicName(partnerDetails.getProfilePicName());
				}
				if(!StringUtils.isEmpty(partnerDetails.getCountry())) {
					partnerDetailsObj.setCountry(partnerDetails.getCountry());
				}

				if(partnerDetails.getPartnerOrgDetails()!=null) {
					Optional<PartnerOrgDetails> partnerOrgDetails = partnerOrgDetailsRepository.findByPartnerId(partnerDetails.getPartnerId());	
					if(partnerOrgDetails.isPresent()) {
						PartnerOrgDetails partnerOrgDetailsObj = partnerOrgDetails.get();
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getCompanyName())) {
							partnerOrgDetailsObj.setCompanyName(partnerDetails.getPartnerOrgDetails().getCompanyName());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getWebsiteUrl())) {
							partnerOrgDetailsObj.setWebsiteUrl(partnerDetails.getPartnerOrgDetails().getWebsiteUrl());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getFacebookUrl())) {
							partnerOrgDetailsObj.setFacebookUrl(partnerDetails.getPartnerOrgDetails().getFacebookUrl());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getLinkedInUrl())) {
							partnerOrgDetailsObj.setLinkedInUrl(partnerDetails.getPartnerOrgDetails().getLinkedInUrl());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getInstagramUrl())) {
							partnerOrgDetailsObj.setInstagramUrl(partnerDetails.getPartnerOrgDetails().getInstagramUrl());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getTwitterUrl())) {
							partnerOrgDetailsObj.setTwitterUrl(partnerDetails.getPartnerOrgDetails().getTwitterUrl());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getTestIsa())) {
							partnerOrgDetailsObj.setTestIsa(partnerDetails.getPartnerOrgDetails().getTestIsa());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getTestGs())) {
							partnerOrgDetailsObj.setTestGs(partnerDetails.getPartnerOrgDetails().getTestGs());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getTestQualifier())) {
							partnerOrgDetailsObj.setTestQualifier(partnerDetails.getPartnerOrgDetails().getTestQualifier());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getEdiIsaId())) {
							partnerOrgDetailsObj.setEdiIsaId(partnerDetails.getPartnerOrgDetails().getEdiIsaId());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getEdiIsaQualifier())) {
							partnerOrgDetailsObj.setEdiIsaQualifier(partnerDetails.getPartnerOrgDetails().getEdiIsaQualifier());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getEdiGsId())) {
							partnerOrgDetailsObj.setEdiGsId(partnerDetails.getPartnerOrgDetails().getEdiGsId());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getCompanyDescription())) {
							partnerOrgDetailsObj.setCompanyDescription(partnerDetails.getPartnerOrgDetails().getCompanyDescription());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getCompanySize())) {
							partnerOrgDetailsObj.setCompanySize(partnerDetails.getPartnerOrgDetails().getCompanySize());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getCompanyType())) {
							partnerOrgDetailsObj.setCompanyType(partnerDetails.getPartnerOrgDetails().getCompanyType());
						}
						if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getCompanyAbout())) {
							partnerOrgDetailsObj.setCompanyAbout(partnerDetails.getPartnerOrgDetails().getCompanyAbout());
						}
						partnerDetailsObj.setPartnerOrgDetails(partnerOrgDetailsObj);
					}
				}
				partnerDetailsObj.setProfilePicData(partnerDetails.getProfilePicData());
				String imageName=null;
				String imgArray[];
				if(!StringUtils.isEmpty(partnerDetails.getProfilePicData())) {
					/**
					 * uploadProfileImage(p1-image data;p2-user first name;p3-profile image path;p4-partnerId;p5-PROFILE to identify source folder)
					 */
					imgArray = partnerDetails.getProfilePicData().split(",");

					imageName = imageUploadUtil.uploadProfileImag(imgArray.length==2?imgArray[1]:imgArray[0], partnerDetails.getProfilePicName()
							,PartnerConstants.PROFILE_IMG_PATH,partnerDetails.getPartnerUserAssociation().getUserId(),"PROFILE");
					partnerDetailsObj.setProfilePicName(imageName);
				}

				//Company Image
				if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getCompanyLogoImgData())) {
					imgArray = partnerDetails.getPartnerOrgDetails().getCompanyLogoImgData().split(",");
					String companyImageName = imageUploadUtil.uploadProfileImag(imgArray.length==2?imgArray[1]:imgArray[0], 
							partnerDetails.getPartnerOrgDetails().getCompanyImageName(), 
							PartnerConstants.PROFILE_IMG_PATH,partnerDetails.getPartnerId(),"PROFILE_BG"); 
					partnerDetailsObj.getPartnerOrgDetails().setCompanyImageName(companyImageName);
				}

				//Company Background Image
				if(!StringUtils.isEmpty(partnerDetails.getPartnerOrgDetails().getCompanyBgImgData())) {
					imgArray = partnerDetails.getPartnerOrgDetails().getCompanyBgImgData().split(",");
					String companyBgImgName = imageUploadUtil.uploadProfileImag(imgArray.length==2?imgArray[1]:imgArray[0], 
							partnerDetails.getPartnerOrgDetails().getCompanyBgImageName(), 
							PartnerConstants.PROFILE_IMG_PATH,partnerDetails.getPartnerId(),"PROFILE");
					partnerDetailsObj.getPartnerOrgDetails().setCompanyBgImageName(companyBgImgName);
				}
				partnerDetailsObj.setUpdatedDate(PartnerUtility.getCurrentDateTime());
				partnerDetailsObj.setUpdatedBy(partnerDetails.getUpdatedBy());
				partnerDetailsRepository.save(partnerDetailsObj);
			}else {

			}
			response = new ApiResponse();
			response.setStatusCode(ResponseCodes.SUCCESS);
			response.setStatusMessage("Partner update is completed sucessfully");
			
			//Add update audit log - Ankitha
			
			return response;
		}catch(DataIntegrityViolationException exception) {
			commonUtil.saveErrorLog(exception);
			return ApiResponse.builder().statusCode(ResponseCodes.DUPLICATE_EMAIL_ADDRESS).statusMessage(partnerConfigProperties.emailDuplicateMessage).build();
		}
	}

	@Override
	public List<PartnerDetails> getFilterPartnerDetails(PartnerDetails partnerDetails) {
		Example<PartnerDetails> partnerExample = Example.of(partnerDetails);
		Iterable<PartnerDetails> partners = partnerDetailsRepository.findAll(partnerExample);
		List<PartnerDetails> listPartners = StreamSupport.stream(partners.spliterator(), false).collect(Collectors.toList());
		if(listPartners.isEmpty()) {
			throw new PartnerNotFoundException(ResponseCodes.SEARCH_PARTNER_FAIL_CODE,partnerConfigProperties.searchPartnerFail);
		}
		return listPartners;
	}
	
	@Override
	public List<PartnerDetails> partnerFreeTextSearch(String keyword) {
		List<PartnerDetails> lstPartnerDetails = partnerDetailsRepository.findPartnerByKeyword(keyword);
		if(lstPartnerDetails.isEmpty()) {
			throw new PartnerNotFoundException(ResponseCodes.SEARCH_PARTNER_FAIL_CODE,partnerConfigProperties.searchPartnerFail);
		}
		return lstPartnerDetails;
	}

	@Override
	public List<PartnerDetails> getPartnerByPartnerIdAndInviteePid(List lstPartnerAssociateIds,Integer initiatedPartnerId) {
		List<PartnerDetails> lstPartnerDetails=new ArrayList<>();
		Iterator<PartnerAssociation> piterator = lstPartnerAssociateIds.iterator();
		while(piterator.hasNext()) {
			PartnerAssociation pAssociation = (PartnerAssociation)piterator.next();
			if(pAssociation.getAssociatedPartnerId()!=initiatedPartnerId) {
				Optional<PartnerDetails> pdetails = partnerDetailsRepository.findByPartnerId(pAssociation.getAssociatedPartnerId());
				if(pdetails.isPresent()) {
					PartnerDetails pd = pdetails.get();
					lstPartnerDetails.add(pd);
				}
			}else if(pAssociation.getSourcePartnerId()!=initiatedPartnerId) {
				if(pAssociation.getAssociatedPartnerId()==initiatedPartnerId)
				{
					Optional<PartnerDetails> pdetailsObj = partnerDetailsRepository.findByPartnerId(pAssociation.getSourcePartnerId());
					if(pdetailsObj.isPresent()) {
						PartnerDetails partnerDet = pdetailsObj.get();
						lstPartnerDetails.add(partnerDet);
					}
				}
			}
		}
		return lstPartnerDetails; 
	}

	@Override
	public PartnerSearchResponse fetchAllPartners(PartnerSearchCriteria ptnrSearchCriteria) {
		PartnerSearchResponse ptnrSearchResponse=null;
		Pageable paging = PageRequest.of(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize());
		Page<PartnerDetails> pagePartnerDetails = partnerDetailsRepository.findAll(paging);
		if(pagePartnerDetails.hasContent()) {
			int totalPages = pagePartnerDetails.getTotalPages();
			List<PartnerDetails> lstPtnrDetails = pagePartnerDetails.getContent();
			ptnrSearchResponse = PartnerSearchResponse.builder()
					.pageble(commonUtil.buildPage(ptnrSearchCriteria.getPageNo(),ptnrSearchCriteria.getPageSize(),totalPages))
					.partnerDetails(lstPtnrDetails)
					.build(); 
		}
		return ptnrSearchResponse;
	}

	@Override
	public PartnerDetails findByPartnerId(Integer partnerId) {
		Optional<PartnerDetails> partnerDettailsOpt = partnerDetailsRepository.findByPartnerId(partnerId);
		if(partnerDettailsOpt.isPresent()) {
			return partnerDettailsOpt.get();
		}else {
			throw new PartnerNotFoundException(ResponseCodes.SEARCH_PARTNER_FAIL_CODE,partnerConfigProperties.searchPartnerFail);
		}
	}
	
}