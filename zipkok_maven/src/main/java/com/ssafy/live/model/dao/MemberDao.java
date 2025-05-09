package com.ssafy.live.model.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ssafy.live.model.dto.Member;
import com.ssafy.live.model.dto.SearchCondition;

public interface MemberDao {
    int insert(Connection con, Member member) throws SQLException;

    Member select(Connection con, String email) throws SQLException;

    List<Member> search(Connection con) throws SQLException;

    public int getTotalCount(Connection con, SearchCondition condition) throws SQLException;

    public List<Member> search(Connection con, SearchCondition condition) throws SQLException;

    public Member selectDetail(Connection con, String email) throws SQLException;

    public int update(Connection con, Member member) throws SQLException;

    public int delete(Connection con, int mno) throws SQLException;
}
