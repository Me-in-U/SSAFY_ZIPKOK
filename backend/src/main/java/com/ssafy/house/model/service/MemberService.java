package com.ssafy.house.model.service;

import java.sql.SQLException;
import java.util.List;

import com.ssafy.house.model.dto.HouseInfo;
import com.ssafy.house.model.dto.HouseRecommend;
import com.ssafy.house.model.dto.Member;
import com.ssafy.house.model.dto.Page;
import com.ssafy.house.model.dto.SearchCondition;

public interface MemberService {
    // 1. 회원가입
    int insert(Member emp) throws SQLException;

    // 2. 회원정보 수정
    void update(Member member) throws SQLException;

    // 3. 회원탈퇴
    void delete(int mno) throws SQLException;

    // 4. 회원 정보 조회
    Member selectByEmail(String email);

    // 5. 회원 전체 조회
    List<Member> selectAll() throws SQLException;

    // 6. 로그인
    Member login(Member member) throws SQLException;

    Page<Member> search(SearchCondition condition);

    // 즐겨찾기
    void addFavorite(int mno, String aptSeq) throws SQLException;

    void removeFavorite(int mno, String aptSeq) throws SQLException;

    List<HouseRecommend> getFavorites(int mno) throws SQLException;

}