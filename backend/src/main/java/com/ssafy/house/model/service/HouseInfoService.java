// src/main/java/com/ssafy/house/model/service/HouseInfoService.java
package com.ssafy.house.model.service;

import java.sql.SQLException;
import java.util.List;

import com.ssafy.house.model.dto.HouseFullInfo;
import com.ssafy.house.model.dto.HouseInfo;
import com.ssafy.house.model.dto.SchoolInfo;

public interface HouseInfoService {
    HouseInfo getHouseInfo(String aptSeq) throws Exception;
    List<HouseInfo> getHouseInfoBySeqList(List<String> aptSeqList) throws Exception;
    List<HouseInfo> getHouseInfoByBounds(
        String minLat, String maxLat, String minLng, String maxLng
    ) throws Exception;
    List<String> searchByAptName(String partialName) throws SQLException;
    List<String> findBySidoGugun(String sido, String gugun) throws Exception;
    List<String> findBySidoGugunDong(String sido, String gugun, String dong) throws Exception;
    List<String> findByOptions(String sido, String gugun, String dong) throws Exception;
    List<String> findByOptionsAndAptName(
        String sido, String gugun, String dong, String aptNm
    ) throws Exception;
    HouseFullInfo getHouseFullInfo(String aptSeq) throws Exception;
    List<SchoolInfo> getSchoolsByAptSeq(String aptSeq) throws Exception;
}
