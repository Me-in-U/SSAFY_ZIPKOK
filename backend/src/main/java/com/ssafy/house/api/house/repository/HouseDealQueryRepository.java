package com.ssafy.house.api.house.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.ssafy.house.api.house.dto.internal.HouseDealView;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

/**
 * 아파트 매물 조회를 담당하는 query repository이다.
 */
@Repository
@RequiredArgsConstructor
public class HouseDealQueryRepository {

    private static final String DEAL_SELECT = """
            SELECT
                hd.deal_id,
                hd.apt_seq,
                hd.listing_name,
                hd.trade_type,
                hd.price,
                hd.property_type,
                hd.spec,
                hd.description,
                DATE_FORMAT(hd.confirmed_at, '%Y-%m-%d') AS confirmed_at,
                hd.deposit,
                hd.monthly_rent
            """;

    private final EntityManager entityManager;

    /**
     * 옵션과 거래유형으로 최근 매물 목록을 조회한다.
     *
     * @param sido 시도명
     * @param gugun 구군명
     * @param dong 동명
     * @param aptNm 아파트명
     * @param tradeType 거래유형
     * @return 매물 목록
     */
    public List<HouseDealView> findDealsByOptionsAndType(
            String sido, String gugun, String dong, String aptNm, String tradeType) {
        StringBuilder sql = new StringBuilder(DEAL_SELECT).append("""
                FROM house_deal hd
                JOIN (
                    SELECT apt_seq, MAX(confirmed_at) AS max_dt
                    FROM house_deal
                """);
        if (StringUtils.hasText(tradeType)) {
            sql.append(" WHERE trade_type = :tradeType");
        }
        sql.append("""
                    GROUP BY apt_seq
                ) latest
                  ON hd.apt_seq = latest.apt_seq
                 AND hd.confirmed_at = latest.max_dt
                JOIN house_info hi
                  ON hi.apt_seq = hd.apt_seq
                JOIN dong_code dc
                  ON LEFT(dc.dong_code, 5) = hi.sgg_cd
                 AND RIGHT(dc.dong_code, 5) = hi.umd_cd
                WHERE 1=1
                """);
        if (StringUtils.hasText(tradeType)) {
            sql.append(" AND hd.trade_type = :tradeType");
        }
        appendDealSearchFilters(sql, sido, gugun, dong, aptNm);

        Query query = entityManager.createNativeQuery(sql.toString());
        if (StringUtils.hasText(tradeType)) {
            query.setParameter("tradeType", tradeType);
        }
        applyDealSearchParameters(query, sido, gugun, dong, aptNm);
        return getDealResultList(query);
    }

    /**
     * 예산 이하의 최근 매매/전세 매물을 조회한다.
     *
     * @param sido 시도명
     * @param gugun 구군명
     * @param dong 동명
     * @param aptNm 아파트명
     * @param maxPrice 최대 가격
     * @return 매물 목록
     */
    public List<HouseDealView> findDealsByBudget(
            String sido, String gugun, String dong, String aptNm, long maxPrice) {
        StringBuilder sql = new StringBuilder(DEAL_SELECT).append("""
                FROM house_deal hd
                JOIN (
                    SELECT apt_seq, MAX(confirmed_at) AS max_dt
                    FROM house_deal
                    WHERE trade_type IN ('매매', '전세')
                    GROUP BY apt_seq
                ) latest
                  ON hd.apt_seq = latest.apt_seq
                 AND hd.confirmed_at = latest.max_dt
                JOIN house_info hi
                  ON hi.apt_seq = hd.apt_seq
                JOIN dong_code dc
                  ON LEFT(dc.dong_code, 5) = hi.sgg_cd
                 AND RIGHT(dc.dong_code, 5) = hi.umd_cd
                WHERE hd.trade_type IN ('매매', '전세')
                  AND hd.price <= :maxPrice
                """);
        appendDealSearchFilters(sql, sido, gugun, dong, aptNm);
        sql.append("""
                ORDER BY ABS(hd.price - :maxPrice)
                LIMIT 100
                """);

        Query query = entityManager.createNativeQuery(sql.toString())
                .setParameter("maxPrice", maxPrice);
        applyDealSearchParameters(query, sido, gugun, dong, aptNm);
        return getDealResultList(query);
    }

    /**
     * 보증금과 월세 한도 이하의 최근 월세 매물을 조회한다.
     *
     * @param sido 시도명
     * @param gugun 구군명
     * @param dong 동명
     * @param aptNm 아파트명
     * @param maxDeposit 최대 보증금
     * @param maxRent 최대 월세
     * @return 매물 목록
     */
    public List<HouseDealView> findRentDeals(
            String sido, String gugun, String dong, String aptNm, long maxDeposit, int maxRent) {
        StringBuilder sql = new StringBuilder(DEAL_SELECT).append("""
                FROM house_deal hd
                JOIN (
                    SELECT apt_seq, MAX(confirmed_at) AS max_dt
                    FROM house_deal
                    WHERE trade_type = '월세'
                    GROUP BY apt_seq
                ) latest
                  ON hd.apt_seq = latest.apt_seq
                 AND hd.confirmed_at = latest.max_dt
                JOIN house_info hi
                  ON hi.apt_seq = hd.apt_seq
                JOIN dong_code dc
                  ON LEFT(dc.dong_code, 5) = hi.sgg_cd
                 AND RIGHT(dc.dong_code, 5) = hi.umd_cd
                WHERE hd.trade_type = '월세'
                  AND hd.deposit <= :maxDeposit
                  AND hd.monthly_rent <= :maxRent
                """);
        appendDealSearchFilters(sql, sido, gugun, dong, aptNm);
        sql.append("""
                ORDER BY ABS(hd.deposit - :maxDeposit) + ABS(hd.monthly_rent - :maxRent)
                LIMIT 100
                """);

        Query query = entityManager.createNativeQuery(sql.toString())
                .setParameter("maxDeposit", maxDeposit)
                .setParameter("maxRent", maxRent);
        applyDealSearchParameters(query, sido, gugun, dong, aptNm);
        return getDealResultList(query);
    }

    /**
     * 조건에 맞는 최저가 최근 매물을 조회한다.
     *
     * @param sido 시도명
     * @param gugun 구군명
     * @param dong 동명
     * @param aptNm 아파트명
     * @param tradeType 거래유형
     * @return 최저가 매물
     */
    public HouseDealView findLowestDeal(
            String sido, String gugun, String dong, String aptNm, String tradeType) {
        StringBuilder sql = new StringBuilder(DEAL_SELECT).append("""
                FROM house_deal hd
                JOIN (
                    SELECT apt_seq, MAX(confirmed_at) AS max_dt
                    FROM house_deal
                    WHERE trade_type = :tradeType
                    GROUP BY apt_seq
                ) latest
                  ON hd.apt_seq = latest.apt_seq
                 AND hd.confirmed_at = latest.max_dt
                JOIN house_info hi
                  ON hi.apt_seq = hd.apt_seq
                JOIN dong_code dc
                  ON LEFT(dc.dong_code, 5) = hi.sgg_cd
                 AND RIGHT(dc.dong_code, 5) = hi.umd_cd
                WHERE hd.trade_type = :tradeType
                """);
        appendDealSearchFilters(sql, sido, gugun, dong, aptNm);
        sql.append(isMonthlyRentTrade(tradeType)
                ? " ORDER BY hd.monthly_rent LIMIT 1"
                : " ORDER BY hd.price LIMIT 1");

        Query query = entityManager.createNativeQuery(sql.toString())
                .setParameter("tradeType", tradeType);
        applyDealSearchParameters(query, sido, gugun, dong, aptNm);
        return getSingleDeal(query);
    }

    /**
     * 조건에 맞는 최고가 최근 매물을 조회한다.
     *
     * @param sido 시도명
     * @param gugun 구군명
     * @param dong 동명
     * @param aptNm 아파트명
     * @param tradeType 거래유형
     * @return 최고가 매물
     */
    public HouseDealView findHighestDeal(
            String sido, String gugun, String dong, String aptNm, String tradeType) {
        StringBuilder sql = new StringBuilder(DEAL_SELECT).append("""
                FROM house_deal hd
                JOIN (
                    SELECT apt_seq, MAX(confirmed_at) AS max_dt
                    FROM house_deal
                    WHERE trade_type = :tradeType
                    GROUP BY apt_seq
                ) latest
                  ON hd.apt_seq = latest.apt_seq
                 AND hd.confirmed_at = latest.max_dt
                JOIN house_info hi
                  ON hi.apt_seq = hd.apt_seq
                JOIN dong_code dc
                  ON LEFT(dc.dong_code, 5) = hi.sgg_cd
                 AND RIGHT(dc.dong_code, 5) = hi.umd_cd
                WHERE hd.trade_type = :tradeType
                """);
        appendDealSearchFilters(sql, sido, gugun, dong, aptNm);
        sql.append(isMonthlyRentTrade(tradeType)
                ? " ORDER BY hd.monthly_rent DESC LIMIT 1"
                : " ORDER BY hd.price DESC LIMIT 1");

        Query query = entityManager.createNativeQuery(sql.toString())
                .setParameter("tradeType", tradeType);
        applyDealSearchParameters(query, sido, gugun, dong, aptNm);
        return getSingleDeal(query);
    }

    /**
     * 조건에 맞는 가격 범위의 최근 매물을 조회한다.
     *
     * @param sido 시도명
     * @param gugun 구군명
     * @param dong 동명
     * @param aptNm 아파트명
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 매물 목록
     */
    public List<HouseDealView> findDealsByPriceRange(
            String sido, String gugun, String dong, String aptNm, long minPrice, long maxPrice) {
        StringBuilder sql = new StringBuilder(DEAL_SELECT).append("""
                FROM house_deal hd
                JOIN (
                    SELECT apt_seq, MAX(confirmed_at) AS max_dt
                    FROM house_deal
                    GROUP BY apt_seq
                ) latest
                  ON hd.apt_seq = latest.apt_seq
                 AND hd.confirmed_at = latest.max_dt
                JOIN house_info hi
                  ON hi.apt_seq = hd.apt_seq
                JOIN dong_code dc
                  ON LEFT(dc.dong_code, 5) = hi.sgg_cd
                 AND RIGHT(dc.dong_code, 5) = hi.umd_cd
                WHERE hd.price BETWEEN :minPrice AND :maxPrice
                """);
        appendDealSearchFilters(sql, sido, gugun, dong, aptNm);
        sql.append(" LIMIT 100");

        Query query = entityManager.createNativeQuery(sql.toString())
                .setParameter("minPrice", minPrice)
                .setParameter("maxPrice", maxPrice);
        applyDealSearchParameters(query, sido, gugun, dong, aptNm);
        return getDealResultList(query);
    }

    private void appendDealSearchFilters(
            StringBuilder sql, String sido, String gugun, String dong, String aptNm) {
        if (StringUtils.hasText(sido)) {
            sql.append(" AND dc.sido_name LIKE CONCAT('%', :sido, '%')");
        }
        if (StringUtils.hasText(gugun)) {
            sql.append(" AND dc.gugun_name LIKE CONCAT('%', :gugun, '%')");
        }
        if (StringUtils.hasText(dong)) {
            sql.append(" AND dc.dong_name LIKE CONCAT('%', :dong, '%')");
        }
        if (StringUtils.hasText(aptNm)) {
            sql.append(" AND hi.apt_nm LIKE CONCAT('%', :aptNm, '%')");
        }
    }

    private void applyDealSearchParameters(Query query, String sido, String gugun, String dong, String aptNm) {
        if (StringUtils.hasText(sido)) {
            query.setParameter("sido", sido);
        }
        if (StringUtils.hasText(gugun)) {
            query.setParameter("gugun", gugun);
        }
        if (StringUtils.hasText(dong)) {
            query.setParameter("dong", dong);
        }
        if (StringUtils.hasText(aptNm)) {
            query.setParameter("aptNm", aptNm);
        }
    }

    @SuppressWarnings("unchecked")
    private List<HouseDealView> getDealResultList(Query query) {
        return ((List<Object[]>) query.getResultList()).stream()
                .map(this::mapToHouseDealView)
                .toList();
    }

    private HouseDealView getSingleDeal(Query query) {
        List<HouseDealView> deals = getDealResultList(query);
        return deals.isEmpty() ? null : deals.getFirst();
    }

    private boolean isMonthlyRentTrade(String tradeType) {
        return "월세".equals(tradeType);
    }

    private HouseDealView mapToHouseDealView(Object[] row) {
        return new HouseDealView(
                row[0] == null ? null : ((Number) row[0]).intValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                row[4] == null ? 0L : ((Number) row[4]).longValue(),
                (String) row[5],
                (String) row[6],
                row[7] == null ? null : row[7].toString(),
                row[8] == null ? null : row[8].toString(),
                row[9] == null ? 0L : ((Number) row[9]).longValue(),
                row[10] == null ? null : ((Number) row[10]).intValue());
    }
}
