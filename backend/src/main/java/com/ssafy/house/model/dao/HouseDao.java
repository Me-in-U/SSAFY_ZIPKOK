package com.ssafy.house.model.dao;

import com.ssafy.house.model.dto.*;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface HouseDao {
    /** 1) 아파트 기본 주소·위치 정보 */
    HouseInfo           selectHouseInfo(String aptSeq);
    /** 2) 아파트 세부 정보 (apt_name, dong, buildYear, lat/lng 등) */
    HouseDetail         selectHouseDetail(String aptSeq);
    /** 3) 거래 내역 (현재/과거 구분 없이 전체) */
    List<HouseDeal>     selectHouseDeals(String aptSeq);
    /** 4) 이미지 목록 */
    List<HouseImage>    selectHouseImages(String aptSeq);
    /** 5) 학군 요약 정보 */
    List<HouseSchool>   selectHouseSchools(String aptSeq);
}
