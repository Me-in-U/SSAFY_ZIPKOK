package com.ssafy.house.model.service;

import com.ssafy.house.model.dao.HouseRecommendDao;
import com.ssafy.house.model.dto.HouseRecommend;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseRecommendServiceImpl implements HouseRecommendService {
    private final HouseRecommendDao recommendDao;

    @Override
    public List<HouseRecommend> getRecentProperties(int limit) {
        // limit 개수 만큼 최근 거래 매물을 DTO 리스트로 반환
        return recommendDao.selectRecentProperties(limit);
    }

    @Override
    public List<HouseRecommend> getMostTradedProperties(int limit) {
        // limit 개수 만큼 최다 거래 매물을 DTO 리스트로 반환
        return recommendDao.selectMostTradedProperties(limit);
    }

    @Override
    public List<HouseRecommend> getRecommendedCompositeProperties(int limit) {
        // limit 개수 만큼 종합추천 매물을 DTO 리스트로 반환
        return recommendDao.selectRecommendedCompositeProperties(limit);
    }
}
