package com.ssafy.house.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.house.model.dao.HouseDealDoneDao;
import com.ssafy.house.model.dto.HouseDealDone;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HouseDealsDoneServiceImpl implements HouseDealsDoneService {

    private final HouseDealDoneDao houseDealsDao;

    @Override
    @Transactional(readOnly = true)
    public List<HouseDealDone> getDealsByAptSeq(String aptSeq) {
        return houseDealsDao.selectDealsByAptSeq(aptSeq);
    }
}