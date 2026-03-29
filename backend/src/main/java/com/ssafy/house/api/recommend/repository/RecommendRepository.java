package com.ssafy.house.api.recommend.repository;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ssafy.house.api.recommend.dto.internal.RecommendProperty;

import lombok.RequiredArgsConstructor;

/**
 * 추천 매물 도메인의 JDBC 조회를 담당하는 repository이다.
 */
@Repository
@RequiredArgsConstructor
public class RecommendRepository {

    private final JdbcClient jdbcClient;

    /**
     * 최근 거래 추천 매물을 조회한다.
     *
     * @param limit 조회 건수
     * @return 추천 매물 목록
     */
    public List<RecommendProperty> getRecentProperties(int limit) {
        return jdbcClient.sql("""
                SELECT
                    p.apt_seq AS aptSeq,
                    hi.img_path AS imgPath,
                    d.listing_name AS listingName,
                    d.price,
                    d.spec,
                    d.property_type AS propertyType,
                    CAST(d.description AS CHAR CHARACTER SET utf8mb4) AS description
                FROM (
                    SELECT apt_seq, MAX(confirmed_at) AS last_date
                    FROM house_deal
                    WHERE price IS NOT NULL
                    GROUP BY apt_seq
                ) p
                JOIN (
                    SELECT apt_seq, MIN(img_path) AS img_path
                    FROM house_image
                    WHERE img_path IS NOT NULL
                    GROUP BY apt_seq
                ) hi ON hi.apt_seq = p.apt_seq
                JOIN house_deal d
                  ON d.apt_seq = p.apt_seq
                 AND d.confirmed_at = p.last_date
                ORDER BY p.last_date DESC
                LIMIT :limit
                """)
                .param("limit", limit)
                .query(RecommendProperty.class)
                .list();
    }

    /**
     * 역세권 추천 매물을 조회한다.
     *
     * @param limit 조회 건수
     * @return 추천 매물 목록
     */
    public List<RecommendProperty> getNearStationProperties(int limit) {
        return jdbcClient.sql("""
                SELECT
                    hd.apt_seq AS aptSeq,
                    hi.img_path AS imgPath,
                    hd.listing_name AS listingName,
                    hd.price,
                    hd.spec,
                    hd.property_type AS propertyType,
                    CAST(hd.description AS CHAR CHARACTER SET utf8mb4) AS description
                FROM house_deal hd
                INNER JOIN house_image hi
                        ON hi.apt_seq = hd.apt_seq
                WHERE hd.description LIKE '%역세권%'
                ORDER BY hd.confirmed_at
                LIMIT :limit
                """)
                .param("limit", limit)
                .query(RecommendProperty.class)
                .list();
    }

    /**
     * 신혼부부 추천 매물을 조회한다.
     *
     * @param limit 조회 건수
     * @return 추천 매물 목록
     */
    public List<RecommendProperty> getNewlywedsProperties(int limit) {
        return jdbcClient.sql("""
                SELECT
                    hd.apt_seq AS aptSeq,
                    hi.img_path AS imgPath,
                    hd.listing_name AS listingName,
                    hd.price,
                    hd.spec,
                    hd.property_type AS propertyType,
                    CAST(hd.description AS CHAR CHARACTER SET utf8mb4) AS description
                FROM house_deal hd
                JOIN (
                    SELECT
                        apt_seq,
                        MIN(
                            CAST(
                                SUBSTRING_INDEX(
                                    SUBSTRING_INDEX(distance, '분', 1),
                                    ' ',
                                    -1
                                ) AS UNSIGNED
                            )
                        ) AS min_distance_minutes
                    FROM house_school
                    GROUP BY apt_seq
                ) hs_min
                  ON hs_min.apt_seq = hd.apt_seq
                INNER JOIN house_image hi
                        ON hi.apt_seq = hd.apt_seq
                WHERE hd.description LIKE '%신혼%'
                ORDER BY hs_min.min_distance_minutes ASC, hd.confirmed_at DESC
                LIMIT :limit
                """)
                .param("limit", limit)
                .query(RecommendProperty.class)
                .list();
    }
}


