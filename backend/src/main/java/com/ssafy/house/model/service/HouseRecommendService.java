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
     * 역세권 매물 조회
     * @param limit 상위 몇 개
     */
    List<HouseRecommend> getNearStationProperties(int limit);

    /**
     * 신혼 추천 매물물 조회
     * @param limit 상위 몇 개
     */
    List<HouseRecommend> getNewlywedsProperties(int limit);
}
