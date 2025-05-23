// src/main/java/com/ssafy/house/model/dao/HouseInfoDao.java
package com.ssafy.house.model.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.house.model.dto.HouseInfoFull;
import com.ssafy.house.model.dto.HouseInfo;
import com.ssafy.house.model.dto.HouseInfoSimple;
import com.ssafy.house.model.dto.SchoolInfo;

@Mapper
public interface HouseInfoDao {
        // 단일 조회
        HouseInfo selectHouseInfo(@Param("aptSeq") String aptSeq) throws SQLException;

        // 범위 조회 (위도·경도 BETWEEN)
        List<HouseInfo> selectByBounds(
                        @Param("minLat") String minLat,
                        @Param("maxLat") String maxLat,
                        @Param("minLng") String minLng,
                        @Param("maxLng") String maxLng) throws SQLException;

        // apt_seq 리스트 조회
        List<HouseInfoSimple> selectBySeqList(List<String> aptSeqList) throws SQLException;

        // 아파트명 일부 검색(GPT)
        List<String> searchByAptName(@Param("partialName") String partialName) throws SQLException;

        // 시도 검색(GPT)
        List<String> findBySido(
                        @Param("sido") String sido) throws SQLException;

        // 시도+구군 검색(GPT)
        List<String> findBySidoGugun(
                        @Param("sido") String sido,
                        @Param("gugun") String gugun) throws SQLException;

        // 시도+구군+동 검색(GPT)
        List<String> findBySidoGugunDong(
                        @Param("sido") String sido,
                        @Param("gugun") String gugun,
                        @Param("dong") String dong) throws SQLException;

        // 조합 검색(GPT)
        List<String> findByOptions(
                        @Param("sido") String sido,
                        @Param("gugun") String gugun,
                        @Param("dong") String dong) throws SQLException;

        // 조합 + 아파트명(GPT)
        List<String> findByOptionsAndAptName(
                        @Param("sido") String sido,
                        @Param("gugun") String gugun,
                        @Param("dong") String dong,
                        @Param("aptNm") String aptNm) throws SQLException;

        // 1) Seq 리스트만 가져오기
        List<String> searchSeqByOptionsAndName(
                        @Param("partialName") String partialName,
                        @Param("sido") String sido,
                        @Param("gugun") String gugun,
                        @Param("dong") String dong,
                        @Param("propertyType") String propertyType,
                        @Param("dealType") String dealType,
                        @Param("builtYear") Integer builtYear,
                        @Param("minPrice") Long minPrice,
                        @Param("maxPrice") Long maxPrice,
                        @Param("areaOption") String areaOption) throws SQLException;

        // selectHouseInfoFull 전체 조회
        HouseInfoFull selectHouseInfoFull(@Param("aptSeq") String aptSeq) throws SQLException;

        // 마커용 조회
        List<HouseInfo> searchMarkersByOptionsAndName(Map<String, Object> params) throws SQLException;

        // 주변 학교 조회
        List<SchoolInfo> selectSchoolsByAptSeq(@Param("aptSeq") String aptSeq) throws SQLException;

        List<HouseInfoSimple> searchFilter(String sido, String gugun, String dong, String aptNm);

}
