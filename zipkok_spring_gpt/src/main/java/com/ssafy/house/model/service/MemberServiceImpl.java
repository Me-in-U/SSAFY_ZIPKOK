package com.ssafy.house.model.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ssafy.house.exception.RecordNotFoundException;
import com.ssafy.house.model.dao.MemberDao;
import com.ssafy.house.model.dto.Member;
import com.ssafy.house.model.dto.Page;
import com.ssafy.house.model.dto.SearchCondition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberDao memberDao;
    private Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    // 1. 회원가입
    @Override
    public int insert(Member member) throws SQLException {
        int result = memberDao.insert(member);
        return result;
    }

    // 2. 회원정보 수정
    @Override
    public void update(Member member) throws SQLException {
        memberDao.update(member);
    }

    // 3. 회원탈퇴
    @Override
    public void delete(int mno) throws SQLException {
        memberDao.delete(mno);
    }

    // 4. 회원 정보 조회
    @Override
    public Member selectByEmail(String email) throws SQLException {
        return memberDao.selectByEmail(email);
    }

    // 5. 전체 회원 조회
    @Override
    public List<Member> selectAll() throws SQLException {
        List<Member> members = memberDao.selectAll();
        if (members == null || members.isEmpty()) {
            logger.info("조회된 회원이 없습니다.");
            throw new RecordNotFoundException("회원이 없습니다.");
        }
        logger.info("조회된 회원 목록: {}", members);
        return members;
    }

    // 6. 로그인
    @Override
    public Member login(Member member) throws SQLException {
        return memberDao.findByIdAndPassword(member);
    }

    @Override
    public Page<Member> search(SearchCondition condition) {
        System.out.println(condition);
        // 전체 회원 수 조회
        int totalItems = memberDao.getTotalCount(condition);
        // 현재 페이지의 회원 목록 조회
        List<Member> members = memberDao.search(condition);
        // 페이지네이션 정보 생성
        Page<Member> page = new Page<>(condition, totalItems, members);
        return page;
    }
}
