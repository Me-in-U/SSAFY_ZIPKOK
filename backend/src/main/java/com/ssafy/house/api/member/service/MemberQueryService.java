package com.ssafy.house.api.member.service;

import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.house.api.member.dto.response.MemberPageResponse;
import com.ssafy.house.api.member.dto.response.MemberProfileResponse;
import com.ssafy.house.api.member.dto.internal.MemberAccount;
import com.ssafy.house.api.member.dto.internal.MemberPageResult;
import com.ssafy.house.api.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

/**
 * 회원 상세, 목록, 삭제를 신규 API 구조에 맞게 제공하는 서비스이다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberRepository memberRepository;

    /**
     * 이메일로 회원을 조회한다.
     *
     * @param email 회원 이메일
     * @return 회원 프로필 응답
     * @throws NoSuchElementException 회원이 존재하지 않는 경우
     */
    public MemberProfileResponse getMemberByEmail(String email) {
        MemberAccount member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new NoSuchElementException("회원 정보를 찾을 수 없습니다.");
        }
        return mapToProfile(member);
    }

    /**
     * 회원 목록을 페이지 단위로 조회한다.
     *
     * @param key 검색 키
     * @param word 검색어
     * @param currentPage 현재 페이지 번호
     * @param itemsPerPage 페이지 크기
     * @return 회원 페이지 응답
     */
    public MemberPageResponse getMembers(String key, String word, int currentPage, int itemsPerPage) {
        MemberPageResult page = memberRepository.search(resolveKey(key), word, currentPage, itemsPerPage);
        return new MemberPageResponse(
                page.members().stream().map(this::mapToProfile).toList(),
                page.currentPage(),
                page.itemsPerPage(),
                page.totalItems(),
                page.totalPages(),
                page.hasPre(),
                page.hasNext(),
                page.startPage(),
                page.endPage());
    }

    /**
     * 회원을 삭제한다.
     *
     * @param mno 회원 번호
     * @throws SQLException 삭제 중 예외가 발생한 경우
     */
    @Transactional
    public void deleteMember(int mno) throws SQLException {
        memberRepository.delete(mno);
    }

    private String resolveKey(String key) {
        if (key == null) {
            return null;
        }
        return Map.of("1", "name", "2", "email").getOrDefault(key, key);
    }

    private MemberProfileResponse mapToProfile(MemberAccount member) {
        return new MemberProfileResponse(member.mno(), member.name(), member.email());
    }
}

