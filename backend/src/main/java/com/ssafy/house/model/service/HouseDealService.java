package com.ssafy.house.model.service;

import java.sql.SQLException;
import java.util.List;
import com.ssafy.house.model.dto.HouseDeal;

public interface HouseDealService {
    /**
     * 시도/구군/읍면동/아파트명/거래유형으로 매물 조회
     * 
     * @param sido      시도 이름 일부 (nullable)
     * @param gugun     구군 이름 일부 (nullable)
     * @param dong      읍면동 이름 일부 (nullable)
     * @param aptNm     아파트 이름 일부 (nullable)
     * @param tradeType 거래유형: 매매/전세/월세 (nullable)
     */
    List<HouseDeal> findDealsByOptionsAndType(String sido, String gugun, String dong, String aptNm, String tradeType)
            throws SQLException;

    // 1. 예산 이하 매매/전세 매물
    List<HouseDeal> findDealsByBudget(
            String sido, String gugun, String dong, String aptNm,
            long maxPrice)
            throws SQLException;

    // 2. 보증금·월세 한도 월세 매물
    List<HouseDeal> findRentDeals(
            String sido, String gugun, String dong, String aptNm,
            long maxDeposit, int maxRent)
            throws SQLException;

    // 3. 옵션 지역별 최저가 매물
    HouseDeal findLowestDeal(
            String sido, String gugun, String dong, String aptNm,
            String tradeType) throws SQLException;

    // 3. 옵션 지역별 최고가 매물
    HouseDeal findHighestDeal(
            String sido, String gugun, String dong, String aptNm,
            String tradeType) throws SQLException;

    // 4. 옵션 지역별 가격 범위 매물
    List<HouseDeal> findDealsByPriceRange(
            String sido, String gugun, String dong, String aptNm,
            long minPrice, long maxPrice) throws SQLException;

}