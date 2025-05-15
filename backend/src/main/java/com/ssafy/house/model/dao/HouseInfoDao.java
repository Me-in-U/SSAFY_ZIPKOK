package com.ssafy.house.model.dao;

import java.sql.SQLException;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ssafy.house.model.dto.HouseInfo;

public interface HouseInfoDao {
    // 전체 조회
    List<HouseInfo> selectHouseInfoAll() throws Exception;

    // 단일 조회
    HouseInfo selectHouseInfo(@Param("aptSeq") String aptSeq) throws Exception;

    // 범위 조회 (위도·경도 BETWEEN)
    List<HouseInfo> selectByBounds(
            @Param("minLat") String minLat,
            @Param("maxLat") String maxLat,
            @Param("minLng") String minLng,
            @Param("maxLng") String maxLng) throws Exception;

    // 삽입
    int insertHouseInfo(HouseInfo info) throws Exception;

    // 수정
    int updateHouseInfo(HouseInfo info) throws Exception;

    // 삭제
    int deleteHouseInfo(@Param("aptSeq") String aptSeq) throws Exception;

    List<HouseInfo> searchByAptName(String partialName) throws SQLException;
}
