// src/main/java/com/ssafy/house/model/service/HouseInfoService.java
package com.ssafy.house.model.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ssafy.house.model.dto.HouseFullInfo;
import com.ssafy.house.model.dto.HouseInfo;
import com.ssafy.house.model.dto.SchoolInfo;

public interface HouseInfoService {
    HouseInfo getHouseInfo(String aptSeq) throws SQLException;

    List<HouseInfo> getHouseInfoBySeqList(List<String> aptSeqList) throws SQLException;

    List<HouseInfo> getHouseInfoByBounds(
            String minLat, String maxLat, String minLng, String maxLng) throws SQLException;

    List<String> searchByAptName(String partialName) throws SQLException;

    List<HouseInfo> searchByName(String partialName) throws SQLException;

    List<String> findBySidoGugun(String sido, String gugun) throws SQLException;

    List<String> findBySidoGugunDong(String sido, String gugun, String dong) throws SQLException;

    List<String> findByOptions(String sido, String gugun, String dong) throws SQLException;

    List<String> findByOptionsAndAptName(
            String sido, String gugun, String dong, String aptNm) throws SQLException;
                
    HouseFullInfo getHouseFullInfo(String aptSeq) throws SQLException;

    List<HouseInfo> getMarkerHouses(Map<String, Object> params) throws SQLException;

    List<SchoolInfo> getSchoolsByAptSeq(String aptSeq) throws SQLException;

}
