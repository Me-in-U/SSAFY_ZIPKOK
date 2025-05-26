package com.ssafy.house.model.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.house.model.dto.HouseDeal;

@Mapper
public interface HouseDealDao {
    List<HouseDeal> findDealsByOptionsAndType(
            @Param("sido") String sido,
            @Param("gugun") String gugun,
            @Param("dong") String dong,
            @Param("aptNm") String aptNm,
            @Param("tradeType") String tradeType) throws SQLException;

    // 예산 이하 매매/전세 매물 (지역·아파트명 옵션 추가)
    List<HouseDeal> findDealsByBudget(
            @Param("sido") String sido,
            @Param("gugun") String gugun,
            @Param("dong") String dong,
            @Param("aptNm") String aptNm,
            @Param("maxPrice") long maxPrice)
            throws SQLException;

    // 보증금·월세 한도 월세 매물 (지역·아파트명 옵션 추가)
    List<HouseDeal> findRentDeals(
            @Param("sido") String sido,
            @Param("gugun") String gugun,
            @Param("dong") String dong,
            @Param("aptNm") String aptNm,
            @Param("maxDeposit") long maxDeposit,
            @Param("maxRent") int maxRent)
            throws SQLException;

    // 3. 옵션 지역별 최저가 매물 (tradeType: "매매","전세","월세")
    HouseDeal findLowestDeal(
            @Param("sido") String sido,
            @Param("gugun") String gugun,
            @Param("dong") String dong,
            @Param("aptNm") String aptNm,
            @Param("tradeType") String tradeType) throws SQLException;

    HouseDeal findHighestDeal(
            @Param("sido") String sido,
            @Param("gugun") String gugun,
            @Param("dong") String dong,
            @Param("aptNm") String aptNm,
            @Param("tradeType") String tradeType) throws SQLException;

    // 4. 옵션 지역별 가격 범위 매물
    List<HouseDeal> findDealsByPriceRange(
            @Param("sido") String sido,
            @Param("gugun") String gugun,
            @Param("dong") String dong,
            @Param("aptNm") String aptNm,
            @Param("minPrice") long minPrice,
            @Param("maxPrice") long maxPrice) throws SQLException;
}
