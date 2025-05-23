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
}