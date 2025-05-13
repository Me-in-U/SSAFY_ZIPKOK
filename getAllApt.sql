SELECT CONCAT(
        d.gugun_name, ' ', d.dong_name, ' ', h.apt_nm
    ) AS full_address
FROM houseinfos h
    JOIN dong_code d ON d.dong_code = CONCAT(h.sgg_cd, h.umd_cd);

CREATE TABLE `housedetails` (
    `apt_seq` VARCHAR(20) NOT NULL COMMENT '아파트코드 (FK → houseinfos)',
    -- 면적 (㎡)
    `area_min` DECIMAL(8, 2) DEFAULT NULL COMMENT '최소면적',
    `area_max` DECIMAL(8, 2) DEFAULT NULL COMMENT '최대면적',
    -- 최근 매매 실거래가 (원)
    `trade_price_min` BIGINT DEFAULT NULL COMMENT '최소 매매가',
    `trade_price_max` BIGINT DEFAULT NULL COMMENT '최대 매매가',
    -- 전세가 (원)
    `jeonse_price_min` BIGINT DEFAULT NULL COMMENT '최소 전세가',
    `jeonse_price_max` BIGINT DEFAULT NULL COMMENT '최대 전세가',
    `last_trade_detail` VARCHAR(100) DEFAULT NULL COMMENT '거래 상세 (날짜·평형·층)',
    PRIMARY KEY (`apt_seq`),
    CONSTRAINT `fk_hd_houseinfo` FOREIGN KEY (`apt_seq`) REFERENCES `houseinfos` (`apt_seq`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '단지 요약 정보 (면적·실거래가·전세가)';

CREATE TABLE `dealslist` (
    `deal_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '매물 고유 ID',
    `apt_seq` VARCHAR(20) NOT NULL COMMENT '아파트코드 (FK → houseinfos)',
    `listing_name` VARCHAR(100) DEFAULT NULL COMMENT '매물명',
    `trade_type` VARCHAR(10) DEFAULT NULL COMMENT '거래종류 (매매/전세/월세 등)',
    `price` BIGINT DEFAULT NULL COMMENT '가격(원)',
    `property_type` VARCHAR(20) DEFAULT NULL COMMENT '유형 (아파트/오피스텔 등)',
    `spec` VARCHAR(100) DEFAULT NULL COMMENT '스펙 (면적/층/방향 등)',
    `description` TEXT COMMENT '상세 설명',
    `confirmed_at` DATE DEFAULT NULL COMMENT '확인일',
    PRIMARY KEY (`deal_id`),
    KEY `idx_deals_apt` (`apt_seq`),
    CONSTRAINT `fk_dl_houseinfo` FOREIGN KEY (`apt_seq`) REFERENCES `houseinfos` (`apt_seq`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '아파트 매물 상세 리스트';

ALTER TABLE dealslist
ADD COLUMN deposit BIGINT DEFAULT NULL COMMENT '월세 보증금(원)',
ADD COLUMN monthly_rent BIGINT DEFAULT NULL COMMENT '월세(원)';

ALTER TABLE `houseinfo_images`
DROP COLUMN `img_hash`,
DROP COLUMN `thumb_path`,
DROP COLUMN `created_at`,
ADD COLUMN `description` varchar(255) DEFAULT NULL AFTER `img_path`;

-- 1) schools 테이블 생성
CREATE TABLE `schools` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '학교 고유 ID',
    `apt_seq` VARCHAR(20) NOT NULL COMMENT '아파트코드 (FK → housedetails)',
    `school_name` VARCHAR(100) NOT NULL COMMENT '학교명',
    `school_type` VARCHAR(50) COMMENT '학교형태 (공립/사립 등)',
    `district` VARCHAR(100) COMMENT '배정동',
    `assignment_detail` VARCHAR(100) COMMENT '배정상세 (전체동 등)',
    `distance` VARCHAR(50) COMMENT '단지에서 학교까지 (ex: 도보로 21분)',
    `address` VARCHAR(255) COMMENT '주소',
    `phone` VARCHAR(50) COMMENT '전화번호',
    `established` VARCHAR(100) COMMENT '설립 정보',
    `office` VARCHAR(100) COMMENT '교육청',
    `teacher_count` VARCHAR(50) COMMENT '교원수',
    `student_count` VARCHAR(50) COMMENT '학생수',
    `homepage` VARCHAR(255) COMMENT '홈페이지 URL',
    PRIMARY KEY (`id`),
    KEY `idx_sch_apt_seq` (`apt_seq`),
    CONSTRAINT `fk_sch_housedetails` FOREIGN KEY (`apt_seq`) REFERENCES `housedetails` (`apt_seq`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '학군정보 테이블';

-- 2) housedetails 테이블에 school_id 컬럼·외래키 추가
ALTER TABLE `housedetails`
ADD COLUMN `school_id` INT NULL AFTER `last_trade_detail`,
ADD CONSTRAINT `fk_hd_school` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`) ON DELETE SET NULL;

CREATE TABLE `school_master` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '학교 고유 ID',
    `school_name` VARCHAR(100) NOT NULL COMMENT '학교명',
    `school_type` VARCHAR(50) DEFAULT NULL COMMENT '학교형태 (공립/사립 등)',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '주소',
    `phone` VARCHAR(50) DEFAULT NULL COMMENT '전화번호',
    `established` VARCHAR(100) DEFAULT NULL COMMENT '설립 정보',
    `office` VARCHAR(100) DEFAULT NULL COMMENT '교육청',
    `teacher_count` VARCHAR(50) DEFAULT NULL COMMENT '교원수',
    `student_count` VARCHAR(50) DEFAULT NULL COMMENT '학생수',
    `homepage` VARCHAR(255) NOT NULL COMMENT '홈페이지 URL',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_school_homepage` (`homepage`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '학교 마스터 테이블';

CREATE TABLE `apt_school` (
    `apt_seq` VARCHAR(20) NOT NULL COMMENT '아파트코드 (FK → housedetails)',
    `school_id` INT NOT NULL COMMENT '학교고유ID (FK → school_master)',
    `district` VARCHAR(100) DEFAULT NULL COMMENT '배정동',
    `assignment_detail` VARCHAR(100) DEFAULT NULL COMMENT '배정상세 (전체동 등)',
    `distance` VARCHAR(50) DEFAULT NULL COMMENT '단지에서 학교까지',
    PRIMARY KEY (`apt_seq`, `school_id`),
    KEY `idx_as_school` (`school_id`),
    CONSTRAINT `fk_as_housedetails` FOREIGN KEY (`apt_seq`) REFERENCES `housedetails` (`apt_seq`) ON DELETE CASCADE,
    CONSTRAINT `fk_as_schoolmaster` FOREIGN KEY (`school_id`) REFERENCES `school_master` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '단지-학교 매핑 테이블';

ALTER TABLE `houseinfo_images` RENAME TO `house_images`;