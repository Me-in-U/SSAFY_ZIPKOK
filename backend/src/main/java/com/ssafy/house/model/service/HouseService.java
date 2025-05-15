package com.ssafy.house.model.service;

import com.ssafy.house.model.dto.*;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.jdbc.SQL;

public interface HouseService {
    /**
     * 아파트 고유번호(aptSeq)로
     * HouseInfo, HouseDetail, HouseDeal, HouseImage, HouseSchool
     * 다섯 가지 정보를 한 번에 묶어서 반환
     */
    HouseAggregate getHouseAggregate(String aptSeq) throws SQLException;
}
