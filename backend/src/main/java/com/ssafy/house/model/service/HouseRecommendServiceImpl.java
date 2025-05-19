package com.ssafy.house.model.service;

import com.ssafy.house.model.dao.HouseRecommendDao;
import com.ssafy.house.model.dto.HouseRecommend;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HouseRecommendServiceImpl implements HouseRecommendService {
    private final HouseRecommendDao recommendDao;

    @Override
    @Cacheable(cacheNames = "recommendRecent", key = "#limit")
    public List<HouseRecommend> getRecentProperties(int limit) {
        // SQL로는 limit * 2 만큼 미리 가져오고, 서비스 레이어에서 단지명(prefix) 중복 제거
        List<HouseRecommend> raw = recommendDao.selectRecentProperties(limit * 10);
        return filterByComplexName(raw, limit);
    }

    @Override
    @Cacheable(cacheNames = "recommendNearStation", key = "#limit")
    public List<HouseRecommend> getNearStationProperties(int limit) {
        List<HouseRecommend> raw = recommendDao.selectNearStationProperties(limit * 10);
        return filterByComplexName(raw, limit);
    }

    @Override
    @Cacheable(cacheNames = "recommendNewlyweds", key = "#limit")
    public List<HouseRecommend> getNewlywedsProperties(int limit) {
        List<HouseRecommend> raw = recommendDao.selectNewlywedsProperties(limit * 10);
        return filterByComplexName(raw, limit);
    }

    /**
     * listingName(예: "초원그린타운 114동")에서 마지막 공백 앞까지를 단지명으로 보고,
     * 같은 단지명은 한 번만 남겨 limit 개수만큼 반환합니다.
     */
    private List<HouseRecommend> filterByComplexName(List<HouseRecommend> raw, int limit) {
        List<HouseRecommend> filtered = new ArrayList<>(limit);
        Set<String> seen = new HashSet<>();
        for (HouseRecommend hr: raw) {
            String name = hr.getListingName();
            int idx = name.lastIndexOf(' ');
            String prefix = idx > 0 ? name.substring(0, idx) : name;
            if (seen.add(prefix)) {
                filtered.add(hr);
                if (filtered.size() >= limit) {
                    break;
                }
            }
        }
        return filtered;
    }
}
