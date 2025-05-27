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
-- Table structure for table `school_detail`
--

DROP TABLE IF EXISTS `school_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `school_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '학교 고유 ID',
  `school_name` varchar(100) NOT NULL COMMENT '학교명',
  `school_type` varchar(50) DEFAULT NULL COMMENT '학교형태 (공립/사립 등)',
  `address` varchar(255) DEFAULT NULL COMMENT '주소',
  `phone` varchar(50) DEFAULT NULL COMMENT '전화번호',
  `established` varchar(100) DEFAULT NULL COMMENT '설립 정보',
  `office` varchar(100) DEFAULT NULL COMMENT '교육청',
  `teacher_count` varchar(50) DEFAULT NULL COMMENT '교원수',
  `student_count` varchar(50) DEFAULT NULL COMMENT '학생수',
  `homepage` varchar(255) NOT NULL COMMENT '홈페이지 URL',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_school_homepage` (`homepage`)
) ENGINE=InnoDB AUTO_INCREMENT=30102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='학교 마스터 테이블';
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
