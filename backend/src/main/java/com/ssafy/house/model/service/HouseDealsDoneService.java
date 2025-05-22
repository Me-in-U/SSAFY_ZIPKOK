package com.ssafy.house.model.service;

import java.util.List;
import com.ssafy.house.model.dto.HouseDealDone;

public interface HouseDealsDoneService {
    /**
     * 특정 aptSeq의 거래 내역(연·월·가격) 리스트
     */
    List<HouseDealDone> getDealsByAptSeq(String aptSeq);
}