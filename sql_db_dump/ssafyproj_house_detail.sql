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
-- Table structure for table `house_detail`
--

DROP TABLE IF EXISTS `house_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `house_detail` (
  `apt_seq` varchar(20) NOT NULL COMMENT '아파트코드 (FK → houseinfos)',
  `area_min` decimal(8,2) DEFAULT NULL COMMENT '최소면적',
  `area_max` decimal(8,2) DEFAULT NULL COMMENT '최대면적',
  `trade_price_min` bigint DEFAULT NULL COMMENT '최소 매매가',
  `trade_price_max` bigint DEFAULT NULL COMMENT '최대 매매가',
  `jeonse_price_min` bigint DEFAULT NULL COMMENT '최소 전세가',
  `jeonse_price_max` bigint DEFAULT NULL COMMENT '최대 전세가',
  `last_trade_detail` varchar(100) DEFAULT NULL COMMENT '거래 상세 (날짜·평형·층)',
  `school_id` int DEFAULT NULL,
  PRIMARY KEY (`apt_seq`),
  KEY `fk_hd_school` (`school_id`),
  CONSTRAINT `fk_hd_houseinfo` FOREIGN KEY (`apt_seq`) REFERENCES `house_info` (`apt_seq`) ON DELETE CASCADE,
  CONSTRAINT `fk_hd_school` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='단지 요약 정보 (면적·실거래가·전세가)';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-27 14:35:39
