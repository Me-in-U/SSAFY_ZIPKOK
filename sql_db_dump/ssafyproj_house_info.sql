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
-- Table structure for table `house_info`
--

DROP TABLE IF EXISTS `house_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `house_info` (
  `apt_seq` varchar(20) NOT NULL COMMENT '아파트코드',
  `sgg_cd` varchar(5) DEFAULT NULL COMMENT '시군구코드',
  `umd_cd` varchar(5) DEFAULT NULL COMMENT '읍면동코드',
  `umd_nm` varchar(20) DEFAULT NULL COMMENT '읍면동이름',
  `jibun` varchar(10) DEFAULT NULL COMMENT '지번',
  `road_nm_sgg_cd` varchar(5) DEFAULT NULL COMMENT '도로명시군구코드',
  `road_nm` varchar(20) DEFAULT NULL COMMENT '도로명',
  `road_nm_bonbun` varchar(10) DEFAULT NULL COMMENT '도로명기초번호',
  `road_nm_bubun` varchar(10) DEFAULT NULL COMMENT '도로명추가번호',
  `apt_nm` varchar(40) DEFAULT NULL COMMENT '아파트이름',
  `build_year` int DEFAULT NULL COMMENT '준공년도',
  `latitude` varchar(45) DEFAULT NULL COMMENT '위도',
  `longitude` varchar(45) DEFAULT NULL COMMENT '경도',
  PRIMARY KEY (`apt_seq`),
  KEY `idx_apt_nm` (`apt_nm`),
  KEY `inx_sgg_cd` (`sgg_cd`),
  KEY `inx_sgg_cd_umd_cd` (`sgg_cd`,`umd_cd`),
  KEY `inx_sgg_cd_umd_cd_umd_nm` (`sgg_cd`,`umd_cd`,`umd_nm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='주택정보테이블';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-27 14:35:45
