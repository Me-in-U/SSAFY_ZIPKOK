package com.ssafy.house.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SidogunguDao {
    List<String> selectDistinctSido();

    List<String> selectDistinctGugun(String sidoName);

    List<String> selectDistinctDong(Map<String, String> params);
}
