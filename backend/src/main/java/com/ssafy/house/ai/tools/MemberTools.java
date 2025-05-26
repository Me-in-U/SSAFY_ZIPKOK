package com.ssafy.house.ai.tools;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import com.ssafy.house.model.dto.Member;
import com.ssafy.house.model.dto.Page;
import com.ssafy.house.model.dto.SearchCondition;
import com.ssafy.house.model.service.MemberService;

import lombok.RequiredArgsConstructor;

// @Component
@Deprecated
@RequiredArgsConstructor
public class MemberTools {
    private final MemberService mService;
    private Logger logger = LoggerFactory.getLogger(MemberTools.class);

    @Tool(description = "이메일을 사용하는 사용자의 정보를 반환한다.")
    public Member getMemberByEmail(@ToolParam(description = "사용자의 이메일") String email) throws SQLException {
        logger.info("getMemberByEmail 호출됨: email={}", email);
        try {
            Member member = mService.selectByEmail(email);
            if (member == null) {
                logger.warn("이메일 '{}'에 해당하는 사용자를 찾을 수 없습니다.", email);
            } else {
                logger.info("이메일 '{}'에 해당하는 사용자 정보: {}", email, member);
            }
            return member;
        } catch (Exception e) {
            logger.error("mService.select(email) 호출 중 예외 발생: email={}", email, e);
            throw e; // 예외를 다시 던져 호출한 쪽에서 처리하도록 함
        }
    }

    // Member 정보를 저장한다.
    @Tool(description = "사용자의 정보를 저장한다.")
    public void saveMember(@ToolParam(description = "사용자의 정보") Member member) throws SQLException {
        logger.info("saveMember 호출됨: member={}", member);
        try {
            mService.insert(member);
            logger.info("mService.insert(member) 호출 성공: member={}", member);
        } catch (Exception e) {
            logger.error("mService.insert(member) 호출 중 예외 발생: member={}", member, e);
            throw e; // 예외를 다시 던져 호출한 쪽에서 처리하도록 함
        }
    }

    // 전체 사용자의 목록을 반환한다.
    @Tool(description = "전체 사용자의 목록을 반환한다.")
    public List<Member> getAllMembers() throws SQLException {
        logger.info("getAllMembers 호출됨");
        try {
            List<Member> members = mService.selectAll();
            logger.info("mService.selectAll() 호출 성공");
            if (members == null || members.isEmpty()) {
                logger.warn("조회된 회원 목록이 비어 있습니다.");
            } else {
                logger.info("조회된 회원 목록: {}", members);
            }
            return members;
        } catch (Exception e) {
            logger.error("mService.selectAll() 호출 중 예외 발생", e);
            throw e; // 예외를 다시 던져 호출한 쪽에서 처리하도록 함
        }
    }

    // 검색 조건인 SearchCondition에 해당하는 데이터를 반환한다.1페이지 당 5개의 정보를 출력한다.
    @Tool(description = "검색 조건에 해당하는 데이터를 반환한다.")
    public Page<Member> getMembersByCondition(@ToolParam(description = "검색 조건") SearchCondition condition) {
        return mService.search(condition);
    }

    // 주어진 mno에 해당하는 Member를 삭제한다.
    @Tool(description = "주어진 mno에 해당하는 Member를 삭제한다.")
    public void deleteMember(@ToolParam(description = "삭제할 Member의 mno") int mno) throws SQLException {
        mService.delete(mno);
    }

    // 전달받은 Member 정보로 회원의 정보를 업데이트 한다.
    @Tool(description = "전달받은 Member 정보로 회원의 정보를 업데이트 한다.")
    public void updateMember(@ToolParam(description = "업데이트할 Member의 정보") Member member) throws SQLException {
        mService.update(member);
    }
}
