-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.17 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for cintap_partner
CREATE DATABASE IF NOT EXISTS `cintap_partner` /*!40100 DEFAULT CHARACTER SET armscii8 COLLATE armscii8_bin */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `cintap_partner`;

-- Dumping structure for table cintap_partner.bpi_user
CREATE TABLE IF NOT EXISTS `bpi_user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'user id',
  `partner_id` int(10) DEFAULT '0',
  `user_name` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `middle_initial` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) NOT NULL,
  `email_address` varchar(50) NOT NULL,
  `contact_number` varchar(15) DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL,
  `permission` varchar(20) DEFAULT NULL,
  `organization` varchar(20) DEFAULT NULL,
  `partner_user_id` int(10) DEFAULT NULL,
  `details_required` tinyint(1) DEFAULT '1',
  `ip_address` varchar(20) DEFAULT NULL,
  `security_code_2fa` int(11) DEFAULT NULL,
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_by` varchar(50) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  PRIMARY KEY (`uid`) USING BTREE,
  UNIQUE KEY `user_email_unique` (`user_name`,`email_address`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cintap_partner.bpi_user_ip
CREATE TABLE IF NOT EXISTS `bpi_user_ip` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `partner_id` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `ip_address` varchar(30) DEFAULT NULL,
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `USER_IP_UNIQUE` (`user_id`,`ip_address`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cintap_partner.email_log
CREATE TABLE IF NOT EXISTS `email_log` (
  `email_log_id` int(10) NOT NULL AUTO_INCREMENT,
  `type` varchar(20) NOT NULL,
  `email_address` varchar(50) NOT NULL,
  `email_body` text NOT NULL,
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_date` varchar(20) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  PRIMARY KEY (`email_log_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cintap_partner.email_template
CREATE TABLE IF NOT EXISTS `email_template` (
  `email_template_id` int(10) NOT NULL AUTO_INCREMENT,
  `source` varchar(50) DEFAULT NULL,
  `type` varchar(50) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `subject` varchar(250) DEFAULT NULL,
  `body` text,
  `description` varchar(1024) DEFAULT NULL,
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_date` varchar(20) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  PRIMARY KEY (`email_template_id`) USING BTREE,
  UNIQUE KEY `unique_template_type` (`type`) USING BTREE,
  UNIQUE KEY `unique_name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cintap_partner.error_log
CREATE TABLE IF NOT EXISTS `error_log` (
  `error_log_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `reference_no` varchar(20) DEFAULT NULL,
  `file_name` varchar(100) DEFAULT NULL,
  `class_name` varchar(100) DEFAULT NULL,
  `fun_name` varchar(100) DEFAULT NULL,
  `error_description` mediumtext,
  `exception` mediumtext,
  `tpId` bigint(10) DEFAULT NULL,
  `bpi_log_Id` bigint(10) DEFAULT NULL,
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`error_log_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cintap_partner.tpd
CREATE TABLE IF NOT EXISTS `tpd` (
  `tpid` int(10) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(100) NOT NULL,
  `middle_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) NOT NULL,
  `primary_email` varchar(100) NOT NULL,
  `primary_telephone` varchar(45) DEFAULT NULL,
  `address_line_1` varchar(255) DEFAULT NULL,
  `address_line_2` varchar(255) DEFAULT NULL,
  `state_name` varchar(50) DEFAULT NULL,
  `city_name` varchar(20) DEFAULT NULL,
  `zip_code` varchar(10) DEFAULT NULL,
  `country` varchar(20) DEFAULT NULL,
  `profile_pic_name` varchar(100) DEFAULT NULL,
  `profile_pic_data` longblob,
  `access_level_id` int(2) DEFAULT NULL,
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_date` varchar(20) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `status_id` int(3) DEFAULT NULL,
  PRIMARY KEY (`tpid`) USING BTREE,
  UNIQUE KEY `unique_email` (`primary_email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=70000001 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cintap_partner.tpexternal
CREATE TABLE IF NOT EXISTS `tpexternal` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `owner_partnerID` int(10) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `details` varchar(100) DEFAULT NULL,
  `website` varchar(100) DEFAULT NULL,
  `line1` varchar(100) DEFAULT NULL,
  `line2` varchar(100) DEFAULT NULL,
  `zipcode` varchar(10) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `state` varchar(2) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `poc_fn` varchar(100) DEFAULT NULL,
  `poc_mi` varchar(10) DEFAULT NULL,
  `poc_ln` varchar(100) DEFAULT NULL,
  `poc_phone` varchar(20) DEFAULT NULL,
  `poc_email` varchar(100) DEFAULT NULL,
  `fax` varchar(100) DEFAULT NULL,
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_date` varchar(30) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=80000001 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cintap_partner.tpinv
CREATE TABLE IF NOT EXISTS `tpinv` (
  `tpinv_id` int(10) NOT NULL AUTO_INCREMENT,
  `organization_name` varchar(100) DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(100) DEFAULT NULL,
  `sender_name` varchar(100) DEFAULT NULL,
  `sender_organization` varchar(100) DEFAULT NULL,
  `sender_partner_id` int(15) DEFAULT NULL,
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_date` varchar(20) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `status` int(5) DEFAULT '7',
  PRIMARY KEY (`tpinv_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cintap_partner.tplink
CREATE TABLE IF NOT EXISTS `tplink` (
  `link_id` int(10) NOT NULL AUTO_INCREMENT,
  `host_pid` int(11) DEFAULT NULL,
  `reject_reason` varchar(500) DEFAULT NULL,
  `invitee_pid` int(11) DEFAULT NULL,
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_date` varchar(20) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `status_id` int(3) DEFAULT NULL,
  PRIMARY KEY (`link_id`) USING BTREE,
  KEY `FK_partner_association` (`status_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cintap_partner.tpod
CREATE TABLE IF NOT EXISTS `tpod` (
  `tp_org_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Trading Partner Ord ID',
  `company_name` varchar(100) DEFAULT NULL,
  `website_url` varchar(100) DEFAULT NULL,
  `company_image_name` varchar(100) DEFAULT NULL,
  `company_bg_image_name` varchar(100) DEFAULT NULL,
  `company_description` varchar(250) DEFAULT NULL,
  `company_size` varchar(50) DEFAULT NULL,
  `company_type` varchar(50) DEFAULT NULL,
  `company_about` varchar(250) DEFAULT NULL,
  `edi_isa_id` varchar(20) DEFAULT NULL,
  `edi_isa_qualifier` varchar(10) DEFAULT NULL,
  `edi_gs_id` varchar(20) DEFAULT NULL,
  `edi_gs_qualifier` varchar(10) DEFAULT NULL,
  `partner_type` varchar(10) DEFAULT NULL,
  `partner_id` int(10) DEFAULT NULL,
  `external_partner_id` int(10) DEFAULT NULL,
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_date` varchar(20) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  PRIMARY KEY (`tp_org_id`) USING BTREE,
  KEY `FK_partner_org_detail` (`partner_id`) USING BTREE,
  CONSTRAINT `FK_partner_org_detail` FOREIGN KEY (`partner_id`) REFERENCES `cintap_bpi`.`tpd` (`tpid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cintap_partner.tpua
CREATE TABLE IF NOT EXISTS `tpua` (
  `uaid` int(2) NOT NULL AUTO_INCREMENT COMMENT 'user association id',
  `uid` int(10) unsigned DEFAULT NULL COMMENT 'user id',
  `tpid` int(10) DEFAULT NULL COMMENT 'trading partner id',
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_date` varchar(20) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  PRIMARY KEY (`uaid`) USING BTREE,
  KEY `FK_partner_user_association` (`tpid`) USING BTREE,
  KEY `FK_partner_user_association1` (`uid`) USING BTREE,
  CONSTRAINT `FK_partner_user_association` FOREIGN KEY (`tpid`) REFERENCES `cintap_bpi`.`tpd` (`tpid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table cintap_partner.tpud
CREATE TABLE IF NOT EXISTS `tpud` (
  `tpid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'trading partner id',
  `first_name` varchar(255) DEFAULT NULL,
  `middle_initial` varchar(50) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `contact_number` varchar(45) DEFAULT NULL,
  `user_email` varchar(45) NOT NULL,
  `password` varchar(45) DEFAULT NULL,
  `zip_code` varchar(45) DEFAULT NULL,
  `organization_name` varchar(45) DEFAULT NULL,
  `access_level_id` int(11) NOT NULL,
  `utid` int(10) DEFAULT NULL COMMENT 'user type id',
  `details_required` tinyint(1) DEFAULT NULL,
  `status_id` int(3) DEFAULT NULL,
  `created_date` varchar(20) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_date` varchar(20) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  PRIMARY KEY (`tpid`) USING BTREE,
  UNIQUE KEY `parnter_user_id_pk` (`tpid`) USING BTREE,
  UNIQUE KEY `email_unique` (`user_email`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
