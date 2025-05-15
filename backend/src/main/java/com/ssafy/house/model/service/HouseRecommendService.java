// src/main/java/com/ssafy/house/service/HouseService.java
package com.ssafy.house.model.service;

import com.ssafy.house.model.dto.HouseAggregate;
import java.util.List;

public interface HouseRecommendService {
    HouseAggregate getHouseAggregate(String aptSeq);

    List<HouseAggregate> getRecentHouses(int limit);
    List<HouseAggregate> getMostTradedHouses(int limit);
    List<HouseAggregate> getRecommendedHouses(int limit);
}
