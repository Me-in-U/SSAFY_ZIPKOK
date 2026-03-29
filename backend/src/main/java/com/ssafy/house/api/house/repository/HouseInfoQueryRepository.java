package com.ssafy.house.api.house.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.ssafy.house.api.house.dto.internal.HouseDetailView;
import com.ssafy.house.api.house.dto.internal.HouseInfoView;
import com.ssafy.house.api.house.dto.internal.HouseSimpleView;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

/**
 * 아파트 기본 정보와 상세 정보 조회를 담당하는 query repository이다.
 */
@Repository
@RequiredArgsConstructor
public class HouseInfoQueryRepository {

    private final EntityManager entityManager;

    /**
     * 지도 범위 내 아파트 목록을 조회한다.
     *
     * @param minLat 최소 위도
     * @param maxLat 최대 위도
     * @param minLng 최소 경도
     * @param maxLng 최대 경도
     * @return 아파트 기본 정보 목록
     */
    @SuppressWarnings("unchecked")
    public List<HouseInfoView> findByBounds(String minLat, String maxLat, String minLng, String maxLng) {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT
                    apt_seq,
                    sgg_cd,
                    umd_cd,
                    umd_nm,
                    jibun,
                    road_nm_sgg_cd,
                    road_nm,
                    road_nm_bonbun,
                    road_nm_bubun,
                    apt_nm,
                    build_year,
                    latitude,
                    longitude
                FROM house_info
                WHERE latitude BETWEEN :minLat AND :maxLat
                  AND longitude BETWEEN :minLng AND :maxLng
                """)
                .setParameter("minLat", minLat)
                .setParameter("maxLat", maxLat)
                .setParameter("minLng", minLng)
                .setParameter("maxLng", maxLng)
                .getResultList();
        return rows.stream().map(this::mapToHouseInfoView).toList();
    }

    /**
     * 아파트 식별자 목록으로 간략 정보를 조회한다.
     *
     * @param aptSeqList 아파트 식별자 목록
     * @return 아파트 간략 정보 목록
     */
    @SuppressWarnings("unchecked")
    public List<HouseSimpleView> findBySeqList(List<String> aptSeqList) {
        if (aptSeqList == null || aptSeqList.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("""
                SELECT DISTINCT
                    apt_seq,
                    apt_nm,
                    latitude,
                    longitude
                FROM house_info
                WHERE apt_seq IN (
                """);
        Map<String, Object> params = new HashMap<>();
        appendInClause(sql, "seq", aptSeqList, params);
        sql.append(")");

        Query query = entityManager.createNativeQuery(sql.toString());
        params.forEach(query::setParameter);
        return ((List<Object[]>) query.getResultList()).stream()
                .map(this::mapToHouseSimpleView)
                .toList();
    }

    /**
     * 아파트 상세 정보를 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 아파트 상세 정보
     */
    @SuppressWarnings("unchecked")
    public HouseDetailView findHouseDetail(String aptSeq) {
        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT
                    hi.apt_seq,
                    hi.apt_nm,
                    hi.latitude,
                    hi.longitude,
                    CONCAT(hi.road_nm,' ',hi.road_nm_bonbun,hi.road_nm_bubun) AS road_address,
                    hi.jibun,
                    hi.build_year,
                    hd.area_min,
                    hd.area_max,
                    hd.trade_price_min,
                    hd.trade_price_max,
                    hd.jeonse_price_min,
                    hd.jeonse_price_max,
                    hd.last_trade_detail,
                    img.img_path,
                    deal.trade_type AS deal_type,
                    deal.deposit,
                    deal.monthly_rent,
                    deal.price AS latest_price,
                    deal.spec AS latest_spec,
                    deal.property_type AS latest_property_type,
                    deal.description AS latest_description
                FROM house_info hi
                LEFT JOIN house_detail hd
                  ON hd.apt_seq = hi.apt_seq
                LEFT JOIN (
                    SELECT apt_seq, MIN(img_path) AS img_path
                    FROM house_image
                    GROUP BY apt_seq
                ) img
                  ON img.apt_seq = hi.apt_seq
                LEFT JOIN (
                    SELECT hd1.apt_seq,
                           hd1.trade_type,
                           hd1.deposit,
                           hd1.monthly_rent,
                           hd1.price,
                           hd1.spec,
                           hd1.property_type,
                           hd1.description
                    FROM house_deal hd1
                    JOIN (
                        SELECT apt_seq, MAX(confirmed_at) AS max_dt
                        FROM house_deal
                        GROUP BY apt_seq
                    ) hd2
                      ON hd1.apt_seq = hd2.apt_seq
                     AND hd1.confirmed_at = hd2.max_dt
                ) deal
                  ON deal.apt_seq = hi.apt_seq
                WHERE hi.apt_seq = :aptSeq
                LIMIT 1
                """)
                .setParameter("aptSeq", aptSeq)
                .getResultList();
        return rows.isEmpty() ? null : mapToHouseDetailView(rows.getFirst());
    }

    /**
     * 조건 기반 아파트 필터 검색을 수행한다.
     *
     * @param sido 시도명
     * @param gugun 구군명
     * @param dong 동명
     * @param aptNm 아파트명
     * @return 아파트 간략 정보 목록
     */
    @SuppressWarnings("unchecked")
    public List<HouseSimpleView> searchFilter(String sido, String gugun, String dong, String aptNm) {
        StringBuilder sql = new StringBuilder("""
                SELECT DISTINCT
                    hi.apt_seq,
                    hi.apt_nm,
                    hi.latitude,
                    hi.longitude
                FROM house_info hi
                JOIN dong_code dc
                  ON LEFT(dc.dong_code, 5) = hi.sgg_cd
                 AND RIGHT(dc.dong_code, 5) = hi.umd_cd
                WHERE hi.apt_nm LIKE CONCAT('%', :aptNm, '%')
                """);
        Query query = entityManager.createNativeQuery(appendLocationFilters(sql, sido, gugun, dong).toString());
        query.setParameter("aptNm", aptNm == null ? "" : aptNm);
        applyLocationParameters(query, sido, gugun, dong);
        return ((List<Object[]>) query.getResultList()).stream()
                .map(this::mapToHouseSimpleView)
                .toList();
    }

    /**
     * 아파트명 일부로 아파트 식별자 목록을 조회한다.
     *
     * @param partialName 아파트명 일부
     * @return 아파트 식별자 목록
     */
    @SuppressWarnings("unchecked")
    public List<String> searchByAptName(String partialName) {
        return entityManager.createNativeQuery("""
                SELECT DISTINCT apt_seq
                FROM house_info
                WHERE apt_nm LIKE :partialName
                """)
                .setParameter("partialName", "%" + partialName + "%")
                .getResultList();
    }

    /**
     * 옵션과 아파트명으로 아파트 식별자 목록을 조회한다.
     *
     * @param sido 시도명
     * @param gugun 구군명
     * @param dong 동명
     * @param aptNm 아파트명
     * @return 아파트 식별자 목록
     */
    @SuppressWarnings("unchecked")
    public List<String> findByOptionsAndAptName(String sido, String gugun, String dong, String aptNm) {
        StringBuilder sql = new StringBuilder("""
                SELECT DISTINCT hi.apt_seq
                FROM house_info hi
                JOIN dong_code dc
                  ON LEFT(dc.dong_code, 5) = hi.sgg_cd
                 AND RIGHT(dc.dong_code, 5) = hi.umd_cd
                WHERE 1=1
                """);
        if (StringUtils.hasText(aptNm)) {
            sql.append(" AND hi.apt_nm LIKE CONCAT('%', :aptNm, '%')");
        }
        appendLocationFilters(sql, sido, gugun, dong);
        Query query = entityManager.createNativeQuery(sql.toString());
        if (StringUtils.hasText(aptNm)) {
            query.setParameter("aptNm", aptNm);
        }
        applyLocationParameters(query, sido, gugun, dong);
        return query.getResultList();
    }
    private StringBuilder appendLocationFilters(StringBuilder sql, String sido, String gugun, String dong) {
        if (StringUtils.hasText(sido)) {
            sql.append(" AND dc.sido_name LIKE CONCAT('%', :sido, '%')");
        }
        if (StringUtils.hasText(gugun)) {
            sql.append(" AND dc.gugun_name LIKE CONCAT('%', :gugun, '%')");
        }
        if (StringUtils.hasText(dong)) {
            sql.append(" AND dc.dong_name LIKE CONCAT('%', :dong, '%')");
        }
        return sql;
    }

    private void applyLocationParameters(Query query, String sido, String gugun, String dong) {
        if (StringUtils.hasText(sido)) {
            query.setParameter("sido", sido);
        }
        if (StringUtils.hasText(gugun)) {
            query.setParameter("gugun", gugun);
        }
        if (StringUtils.hasText(dong)) {
            query.setParameter("dong", dong);
        }
    }

    private void appendInClause(StringBuilder sql, String prefix, List<String> values, Map<String, Object> params) {
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            String paramName = prefix + i;
            sql.append(":").append(paramName);
            params.put(paramName, values.get(i));
        }
    }

    private HouseInfoView mapToHouseInfoView(Object[] row) {
        return new HouseInfoView(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                (String) row[5],
                (String) row[6],
                row[7] == null ? null : row[7].toString(),
                row[8] == null ? null : row[8].toString(),
                (String) row[9],
                row[10] == null ? null : ((Number) row[10]).intValue(),
                row[11] == null ? null : row[11].toString(),
                row[12] == null ? null : row[12].toString());
    }

    private HouseSimpleView mapToHouseSimpleView(Object[] row) {
        return new HouseSimpleView(
                (String) row[0],
                (String) row[1],
                row[2] == null ? null : row[2].toString(),
                row[3] == null ? null : row[3].toString());
    }

    private HouseDetailView mapToHouseDetailView(Object[] row) {
        return new HouseDetailView(
                (String) row[0],
                (String) row[1],
                row[2] == null ? null : row[2].toString(),
                row[3] == null ? null : row[3].toString(),
                (String) row[4],
                (String) row[5],
                row[6] == null ? null : ((Number) row[6]).intValue(),
                row[7] == null ? null : ((Number) row[7]).doubleValue(),
                row[8] == null ? null : ((Number) row[8]).doubleValue(),
                row[9] == null ? null : ((Number) row[9]).longValue(),
                row[10] == null ? null : ((Number) row[10]).longValue(),
                row[11] == null ? null : ((Number) row[11]).longValue(),
                row[12] == null ? null : ((Number) row[12]).longValue(),
                (String) row[13],
                (String) row[14],
                (String) row[15],
                row[16] == null ? null : ((Number) row[16]).longValue(),
                row[17] == null ? null : ((Number) row[17]).longValue(),
                row[18] == null ? null : ((Number) row[18]).longValue(),
                (String) row[19],
                (String) row[20],
                row[21] == null ? null : row[21].toString());
    }
}
