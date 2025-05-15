package com.ssafy.house.model.service;

import java.util.List;
import com.ssafy.house.model.dto.HouseInfo;

public interface HouseInfoService {
    List<HouseInfo> getAllHouseInfo() throws Exception;

    HouseInfo getHouseInfo(String aptSeq) throws Exception;

    List<HouseInfo> getHouseInfoByBounds(String minLat, String maxLat, String minLng, String maxLng) throws Exception;

    boolean addHouseInfo(HouseInfo info) throws Exception;

    boolean modifyHouseInfo(HouseInfo info) throws Exception;

    boolean removeHouseInfo(String aptSeq) throws Exception;
}
