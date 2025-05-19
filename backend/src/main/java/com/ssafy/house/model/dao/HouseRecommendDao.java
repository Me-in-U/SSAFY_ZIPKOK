package com.ssafy.house.model.dao;

import java.util.List;

import com.ssafy.house.model.dto.HouseRecommend;

public interface HouseRecommendDao {
    List<HouseRecommend> selectRecentProperties(int limit);
    List<HouseRecommend> selectNearStationProperties(int limit);
    List<HouseRecommend> selectNewlywedsProperties(int limit);
}

