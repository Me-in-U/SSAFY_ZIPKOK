package com.ssafy.house.model.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.house.model.dto.Member;
import com.ssafy.house.model.dto.SearchCondition;

@Mapper
public interface MemberDao {
    // 1. 회원가입
    int insert(Member member);

    // 2. 회원 정보 수정
    int update(Member member);

    // 3. 회원탈퇴
    int delete(int mno);

    // 4. 회원 정보 조회
    Member selectByEmail(String email);

    // 5. 전체 회원 조회
    List<Member> selectAll();

    // 6. 로그인
    // 아이디와 비밀번호로 회원정보 조회
    Member findByIdAndPassword(Member member) throws SQLException;

    public int getTotalCount(SearchCondition condition);

    public List<Member> search(SearchCondition condition);
}
