package com.ssafy.house.api.member.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ssafy.house.api.member.dto.internal.MemberFavoriteSummary;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * 회원 즐겨찾기 목록 조회를 담당하는 query repository이다.
 */
@Repository
@RequiredArgsConstructor
public class MemberFavoriteQueryRepository {

    private final EntityManager entityManager;

    /**
     * 회원 즐겨찾기 목록을 조회한다.
     *
     * @param mno 회원 번호
     * @return 즐겨찾기 목록
     */
    @SuppressWarnings("unchecked")
    public List<MemberFavoriteSummary> findFavorites(int mno) {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT
                    mf.apt_seq AS aptSeq,
                    img.img_path AS imgPath,
                    hi.apt_nm AS listingName,
                    hd.price AS price,
                    hd.spec AS spec,
                    hd.property_type AS propertyType,
                    CAST(hd.description AS CHAR CHARACTER SET utf8mb4) AS description
                FROM member_favorite mf
                JOIN house_info hi
                  ON hi.apt_seq = mf.apt_seq
                LEFT JOIN house_deal hd
                  ON hd.deal_id = (
                    SELECT d2.deal_id
                    FROM house_deal d2
                    WHERE d2.apt_seq = mf.apt_seq
                    ORDER BY d2.confirmed_at DESC, d2.deal_id ASC
                    LIMIT 1
                  )
                LEFT JOIN (
                    SELECT hi2.apt_seq, hi2.img_path
                    FROM house_image hi2
                    WHERE hi2.id = (
                        SELECT MIN(hi3.id)
                        FROM house_image hi3
                        WHERE hi3.apt_seq = hi2.apt_seq
                    )
                ) img
                  ON img.apt_seq = mf.apt_seq
                WHERE mf.mno = :mno
                """)
                .setParameter("mno", mno)
                .getResultList();

        return rows.stream()
                .map(this::mapToSummary)
                .toList();
    }

    private MemberFavoriteSummary mapToSummary(Object[] row) {
        return new MemberFavoriteSummary(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                row[3] == null ? null : ((Number) row[3]).longValue(),
                (String) row[4],
                (String) row[5],
                row[6] == null ? null : row[6].toString());
    }
}
