package com.ssafy.house.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.house.model.dto.HouseInfo;
import com.ssafy.house.model.dto.HouseRecommend;

@Mapper
public interface MemberFavoriteDao {
    // 즐겨찾기 추가
    int insertFavorite(@Param("mno") int mno, @Param("aptSeq") String aptSeq);

    // 즐겨찾기 삭제
    int deleteFavorite(@Param("mno") int mno, @Param("aptSeq") String aptSeq);

    // 회원의 즐겨찾기 목록 조회 (HouseInfo 반환)
    List<HouseRecommend> selectFavoritesByMember(@Param("mno") int mno);
}