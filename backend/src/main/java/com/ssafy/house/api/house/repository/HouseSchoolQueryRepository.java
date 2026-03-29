package com.ssafy.house.api.house.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ssafy.house.api.house.dto.internal.SchoolInfoView;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * 아파트 주변 학교 조회를 담당하는 query repository이다.
 */
@Repository
@RequiredArgsConstructor
public class HouseSchoolQueryRepository {

    private final EntityManager entityManager;

    /**
     * 아파트 주변 학교 목록을 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 학교 목록
     */
    @SuppressWarnings("unchecked")
    public List<SchoolInfoView> findSchoolsByAptSeq(String aptSeq) {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT
                    sd.school_name,
                    sd.school_type,
                    hs.distance
                FROM house_school hs
                JOIN school_detail sd
                  ON sd.id = hs.school_id
                WHERE hs.apt_seq = :aptSeq
                ORDER BY CAST(SUBSTRING_INDEX(distance,'분',1) AS UNSIGNED)
                """)
                .setParameter("aptSeq", aptSeq)
                .getResultList();
        return rows.stream()
                .map(row -> new SchoolInfoView((String) row[0], (String) row[1], (String) row[2]))
                .toList();
    }
}
