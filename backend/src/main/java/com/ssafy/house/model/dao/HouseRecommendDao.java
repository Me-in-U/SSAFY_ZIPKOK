package com.ssafy.house.model.dao;

import java.util.List;

import com.ssafy.house.model.dto.HouseRecommend;

public interface HouseRecommendDao {
    List<HouseRecommend> selectRecentProperties(int limit);
    List<HouseRecommend> selectMostTradedProperties(int limit);
    List<HouseRecommend> selectRecommendedCompositeProperties(int limit);
}

