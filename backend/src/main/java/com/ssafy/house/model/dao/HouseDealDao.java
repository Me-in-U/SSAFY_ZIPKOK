package com.ssafy.house.model.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.house.model.dto.HouseDeal;

@Mapper
public interface HouseDealDao {
    List<HouseDeal> findDealsByOptionsAndType(
            @Param("sido") String sido,
            @Param("gugun") String gugun,
            @Param("dong") String dong,
            @Param("aptNm") String aptNm,
            @Param("tradeType") String tradeType) throws SQLException;
}
