package com.ssafy.house.model.service;

import com.ssafy.house.model.dao.HouseDao;
import com.ssafy.house.model.dto.*;
import com.ssafy.house.model.dto.HouseAggregate;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService {

    private final HouseDao houseDao;

    @Override
    public HouseAggregate getHouseAggregate(String aptSeq) {
        HouseInfo info       = houseDao.selectHouseInfo(aptSeq);
        HouseDetail detail   = houseDao.selectHouseDetail(aptSeq);
        var deals            = houseDao.selectHouseDeals(aptSeq);
        var images           = houseDao.selectHouseImages(aptSeq);
        var schools          = houseDao.selectHouseSchools(aptSeq);

        return new HouseAggregate(info, detail, deals, images, schools);
    }
}
