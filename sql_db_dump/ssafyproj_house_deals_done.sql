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
-- Table structure for table `house_deals_done`
--

DROP TABLE IF EXISTS `house_deals_done`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `house_deals_done` (
  `no` int NOT NULL AUTO_INCREMENT COMMENT '거래번호',
  `apt_seq` varchar(20) DEFAULT NULL COMMENT '아파트코드',
  `apt_dong` varchar(40) DEFAULT NULL COMMENT '아파트동',
  `floor` varchar(3) DEFAULT NULL COMMENT '아파트층',
  `deal_year` int DEFAULT NULL COMMENT '거래년도',
  `deal_month` int DEFAULT NULL COMMENT '거래월',
  `deal_day` int DEFAULT NULL COMMENT '거래일',
  `exclu_use_ar` decimal(7,2) DEFAULT NULL COMMENT '아파트면적',
  `deal_amount` varchar(10) DEFAULT NULL COMMENT '거래가격',
  PRIMARY KEY (`no`),
  KEY `idx_apt_seq` (`apt_seq`),
  CONSTRAINT `apt_seq_to_house_info` FOREIGN KEY (`apt_seq`) REFERENCES `house_info` (`apt_seq`)
) ENGINE=InnoDB AUTO_INCREMENT=7262332 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='주택거래정보테이블';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-27 14:35:46
