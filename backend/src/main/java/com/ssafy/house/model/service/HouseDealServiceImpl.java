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
}