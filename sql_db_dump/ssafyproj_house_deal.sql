-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: ssafy.blog    Database: ssafyproj
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `house_deal`
--

DROP TABLE IF EXISTS `house_deal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `house_deal` (
  `deal_id` bigint NOT NULL AUTO_INCREMENT COMMENT '매물 고유 ID',
  `apt_seq` varchar(20) NOT NULL COMMENT '아파트코드 (FK → houseinfos)',
  `listing_name` varchar(100) DEFAULT NULL COMMENT '매물명',
  `trade_type` varchar(10) DEFAULT NULL COMMENT '거래종류 (매매/전세/월세 등)',
  `price` bigint DEFAULT NULL COMMENT '가격(원)',
  `property_type` varchar(20) DEFAULT NULL COMMENT '유형 (아파트/오피스텔 등)',
  `spec` varchar(100) DEFAULT NULL COMMENT '스펙 (면적/층/방향 등)',
  `description` text COMMENT '상세 설명',
  `confirmed_at` date DEFAULT NULL COMMENT '확인일',
  `deposit` bigint DEFAULT NULL COMMENT '월세 보증금(원)',
  `monthly_rent` bigint DEFAULT NULL COMMENT '월세(원)',
  PRIMARY KEY (`deal_id`),
  KEY `idx_deals_apt` (`apt_seq`),
  KEY `idx_hd_apt_confirmed` (`apt_seq`,`confirmed_at`),
  KEY `idx_hd_price_apt` (`price`,`apt_seq`),
  CONSTRAINT `fk_dl_houseinfo` FOREIGN KEY (`apt_seq`) REFERENCES `house_info` (`apt_seq`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=332863 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='아파트 매물 상세 리스트';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-27 14:35:42
