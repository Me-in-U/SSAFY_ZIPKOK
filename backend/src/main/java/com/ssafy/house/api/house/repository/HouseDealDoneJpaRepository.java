package com.ssafy.house.api.house.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.house.api.house.entity.HouseDealDoneEntity;

/**
 * 거래 완료 이력 엔티티의 기본 CRUD를 제공하는 JPA repository이다.
 */
public interface HouseDealDoneJpaRepository extends JpaRepository<HouseDealDoneEntity, Integer> {

    /**
     * 아파트 식별자로 거래 완료 이력을 정렬 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 거래 완료 이력 목록
     */
    List<HouseDealDoneEntity> findAllByAptSeqOrderByDealYearAscDealMonthAscDealDayAsc(String aptSeq);
}
