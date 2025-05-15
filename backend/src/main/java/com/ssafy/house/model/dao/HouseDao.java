package com.ssafy.house.model.dao;

import com.ssafy.house.model.dto.*;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface HouseDao {
    HouseDetail        selectHouseDetail(String aptSeq);
    HouseInfo selectHouseInfo(String aptSeq);
    List<HouseDeal>    selectCurrentDeals(String aptSeq);

    List<HouseDealsDone> selectPastDeals(String aptSeq);

    List<HouseImage> selectImages(String aptSeq);

    List<HouseSchool> selectSchoolInfos(String aptSeq);

    List<SchoolDetail> selectSchoolDetails(String schoolId);
}
