// src/main/java/com/ssafy/house/model/dao/HouseDao.java
package com.ssafy.house.model.dao;

import java.util.List;

public interface HouseRecommendDao {

    // (기존 메서드…)

    /** 1) 최근 거래된 아파트 시퀀스 목록 (limit 개수) */
    List<String> selectRecentAptSeqs(int limit);

    /** 2) 최다 거래된 아파트 시퀀스 목록 (limit 개수) */
    List<String> selectMostTradedAptSeqs(int limit);

    /** 3) 종합점수 기반 추천 아파트 시퀀스 목록 (limit 개수) */
    List<String> selectRecommendedCompositeApts(int limit);
}
