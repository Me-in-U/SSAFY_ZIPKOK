// src/main/java/com/ssafy/house/service/HouseService.java
package com.ssafy.house.model.service;

import com.ssafy.house.model.dto.HouseRecommend;
import java.util.List;

public interface HouseRecommendService {
    /**
     * 최근 거래 매물 조회
     * @param limit 상위 몇 개
     */
    List<HouseRecommend> getRecentProperties(int limit);

    /**
     * 최다 거래 매물 조회
     * @param limit 상위 몇 개
     */
    List<HouseRecommend> getMostTradedProperties(int limit);

    /**
     * 종합추천 매물 조회
     * @param limit 상위 몇 개
     */
    List<HouseRecommend> getRecommendedCompositeProperties(int limit);
}
