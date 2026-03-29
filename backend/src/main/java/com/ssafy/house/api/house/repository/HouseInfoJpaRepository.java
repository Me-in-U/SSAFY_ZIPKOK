package com.ssafy.house.api.house.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.house.api.house.entity.HouseInfoEntity;

/**
 * 아파트 기본 정보 엔티티의 기본 CRUD를 제공하는 JPA repository이다.
 */
public interface HouseInfoJpaRepository extends JpaRepository<HouseInfoEntity, String> {
}
