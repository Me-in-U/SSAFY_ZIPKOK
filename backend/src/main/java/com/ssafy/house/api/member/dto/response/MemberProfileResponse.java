package com.ssafy.house.api.member.dto.response;

/**
 * 회원 기본 정보 응답이다.
 *
 * @param mno 회원 번호
 * @param name 회원 이름
 * @param email 회원 이메일
 */
public record MemberProfileResponse(
        int mno,
        String name,
        String email) {
}
