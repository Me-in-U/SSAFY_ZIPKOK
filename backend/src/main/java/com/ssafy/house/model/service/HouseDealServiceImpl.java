package com.ssafy.house.model.service;

import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.ssafy.house.model.dto.HouseDeal;
import com.ssafy.house.model.dao.HouseDealDao;

@Service
@RequiredArgsConstructor
public class HouseDealServiceImpl implements HouseDealService {
    private final HouseDealDao houseDealDao;

    @Override
    public List<HouseDeal> findDealsByOptionsAndType(String sido, String gugun, String dong, String aptNm,
            String tradeType)
            throws SQLException {
        return houseDealDao.findDealsByOptionsAndType(sido, gugun, dong, aptNm, tradeType);
    }

    @Override
    public List<HouseDeal> findDealsByBudget(
            String sido, String gugun, String dong, String aptNm,
            long maxPrice)
            throws SQLException {
        return houseDealDao.findDealsByBudget(sido, gugun, dong, aptNm, maxPrice);
    }

    @Override
    public List<HouseDeal> findRentDeals(
            String sido, String gugun, String dong, String aptNm,
            long maxDeposit, int maxRent)
            throws SQLException {
        return houseDealDao.findRentDeals(sido, gugun, dong, aptNm, maxDeposit, maxRent);
    }

    @Override
    public HouseDeal findLowestDeal(
            String sido, String gugun, String dong, String aptNm,
            String tradeType) throws SQLException {
        return houseDealDao.findLowestDeal(sido, gugun, dong, aptNm, tradeType);
    }

    @Override
    public HouseDeal findHighestDeal(
            String sido, String gugun, String dong, String aptNm,
            String tradeType) throws SQLException {
        return houseDealDao.findHighestDeal(sido, gugun, dong, aptNm, tradeType);
    }

    @Override
    public List<HouseDeal> findDealsByPriceRange(
            String sido, String gugun, String dong, String aptNm,
            long minPrice, long maxPrice) throws SQLException {
        return houseDealDao.findDealsByPriceRange(
                sido, gugun, dong, aptNm, minPrice, maxPrice);
    }

}