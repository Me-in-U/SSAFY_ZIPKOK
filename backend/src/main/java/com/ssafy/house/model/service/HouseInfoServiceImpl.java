package com.ssafy.house.model.service;

import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ssafy.house.model.dao.HouseInfoDao;
import com.ssafy.house.model.dto.HouseInfo;

@Service
public class HouseInfoServiceImpl implements HouseInfoService {

    @Autowired
    private HouseInfoDao dao;

    @Override
    public List<HouseInfo> getAllHouseInfo() throws Exception {
        return dao.selectHouseInfoAll();
    }

    @Override
    public HouseInfo getHouseInfo(String aptSeq) throws Exception {
        return dao.selectHouseInfo(aptSeq);
    }

    @Override
    public List<HouseInfo> getHouseInfoByBounds(String minLat, String maxLat, String minLng, String maxLng)
            throws Exception {
        return dao.selectByBounds(minLat, maxLat, minLng, maxLng);
    }

    @Override
    public boolean addHouseInfo(HouseInfo info) throws Exception {
        return dao.insertHouseInfo(info) == 1;
    }

    @Override
    public boolean modifyHouseInfo(HouseInfo info) throws Exception {
        return dao.updateHouseInfo(info) == 1;
    }

    @Override
    public boolean removeHouseInfo(String aptSeq) throws Exception {
        return dao.deleteHouseInfo(aptSeq) == 1;
    }

    @Override
    public List<HouseInfo> searchByAptName(String partialName) throws SQLException {
        return dao.searchByAptName("%" + partialName + "%"); // 양쪽 wildcard
    }

}
