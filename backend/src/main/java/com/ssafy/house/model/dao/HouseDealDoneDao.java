package com.ssafy.house.model.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.house.model.dto.HouseDealDone;

@Mapper
public interface HouseDealDoneDao {
    /**
     * apt_seq 에 해당하는 모든 거래 내역 조회 (연·월 순)
     */
    List<HouseDealDone> selectDealsByAptSeq(@Param("aptSeq") String aptSeq);
}