package com.ssafy.house.model.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.ssafy.house.model.dto.HouseInfo;

public interface HouseInfoDao {
    // 단일 조회
    HouseInfo selectHouseInfo(@Param("aptSeq") String aptSeq) throws Exception;

    // 범위 조회 (위도·경도 BETWEEN)
    List<HouseInfo> selectByBounds(
            @Param("minLat") String minLat,
            @Param("maxLat") String maxLat,
            @Param("minLng") String minLng,
            @Param("maxLng") String maxLng) throws Exception;

    List<HouseInfo> selectBySeqList(List<String> aptSeqList);

    // 아파트명 일부 검색
    List<String> searchByAptName(@Param("partialName") String partialName) throws SQLException;

    // 시도+구군 검색
    List<String> findBySidoGugun(
            @Param("sido") String sido,
            @Param("gugun") String gugun);

    // 시도+구군+동 검색
    List<String> findBySidoGugunDong(
            @Param("sido") String sido,
            @Param("gugun") String gugun,
            @Param("dong") String dong);

    // 조합 검색 (null 파라미터는 무시)
    List<String> findByOptional(
            @Param("sido") String sido,
            @Param("gugun") String gugun,
            @Param("dong") String dong);

    // 조합 + 아파트명 검색
    List<String> findByFiltersAndName(
            @Param("sido") String sido,
            @Param("gugun") String gugun,
            @Param("dong") String dong,
            @Param("aptNm") String aptNm);
}
