package com.ssafy.house.model.service;

import java.sql.SQLException;
import java.util.List;
import com.ssafy.house.model.dto.HouseInfo;

public interface HouseInfoService {
    HouseInfo getHouseInfo(String aptSeq) throws Exception;

    /** apt_seq 목록으로 한 번에 상세정보 조회 */
    List<HouseInfo> getHouseInfoBySeqList(List<String> aptSeqList) throws SQLException;

    List<HouseInfo> getHouseInfoByBounds(String minLat, String maxLat, String minLng, String maxLng) throws Exception;

    List<String> searchByAptName(String partialName) throws SQLException;

    List<String> findBySidoGugun(String sido, String gugun);

    List<String> findBySidoGugunDong(String sido, String gugun, String dong);

    List<String> findByOptions(String sido, String gugun, String dong);

    List<String> findByOptionsAndAptName(String sido, String gugun, String dong, String aptNm);
}