package com.ssafy.house.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.house.model.dto.Dong;
import com.ssafy.house.model.dto.Gugun;
import com.ssafy.house.model.dto.Sido;

@Mapper
public interface SidogunguDao {
    List<String> selectDistinctSido();

    List<String> selectDistinctGugun(String sidoName);

    List<String> selectDistinctDong(Map<String, String> params);
}
