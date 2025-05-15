package com.ssafy.house.model.service;

import com.ssafy.house.model.dao.HouseRecommendDao;
import com.ssafy.house.model.dto.HouseAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseRecommendServiceImpl implements HouseRecommendService {
    @Autowired
    private HouseRecommendDao houseRecommendDao;
    private HouseRecommendService self; // 순환 호출 방지 없이 동일 impl 사용


    @Override
    public HouseAggregate getHouseAggregate(String aptSeq) {
        return self.getHouseAggregate(aptSeq); // 기존 구현 재사용
    }

    @Override
    public List<HouseAggregate> getRecentHouses(int limit) {
        List<String> seqs = houseRecommendDao.selectRecentAptSeqs(limit);
        return seqs.stream()
                .map(self::getHouseAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<HouseAggregate> getMostTradedHouses(int limit) {
        List<String> seqs = houseRecommendDao.selectMostTradedAptSeqs(limit);
        return seqs.stream()
                .map(self::getHouseAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<HouseAggregate> getRecommendedHouses(int limit) {
        List<String> seqs = houseRecommendDao.selectRecommendedCompositeApts(limit);
        return seqs.stream()
                .map(self::getHouseAggregate)
                .collect(Collectors.toList());
    }
}
