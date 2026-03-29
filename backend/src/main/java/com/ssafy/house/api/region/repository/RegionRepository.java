package com.ssafy.house.api.region.repository;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

/**
 * 행정구역 도메인의 JDBC 조회를 담당하는 repository이다.
 */
@Repository
@RequiredArgsConstructor
public class RegionRepository {

    private final JdbcClient jdbcClient;

    /**
     * 전체 시도 목록을 조회한다.
     *
     * @return 시도 목록
     */
    public List<String> getAllSido() {
        return jdbcClient.sql("""
                SELECT DISTINCT sido_name
                FROM dong_code
                ORDER BY sido_name
                """)
                .query(String.class)
                .list();
    }

    /**
     * 시도명으로 구군 목록을 조회한다.
     *
     * @param sidoName 시도명
     * @return 구군 목록
     */
    public List<String> getGugunBySido(String sidoName) {
        return jdbcClient.sql("""
                SELECT DISTINCT gugun_name
                FROM dong_code
                WHERE sido_name = :sidoName
                  AND gugun_name IS NOT NULL
                ORDER BY gugun_name
                """)
                .param("sidoName", sidoName)
                .query(String.class)
                .list();
    }

    /**
     * 시도와 구군명으로 동 목록을 조회한다.
     *
     * @param sidoName 시도명
     * @param gugunName 구군명
     * @return 동 목록
     */
    public List<String> getDongBySidoAndGugun(String sidoName, String gugunName) {
        return jdbcClient.sql("""
                SELECT DISTINCT dong_name
                FROM dong_code
                WHERE sido_name = :sidoName
                  AND gugun_name = :gugunName
                  AND dong_name IS NOT NULL
                ORDER BY dong_name
                """)
                .param("sidoName", sidoName)
                .param("gugunName", gugunName)
                .query(String.class)
                .list();
    }
}

