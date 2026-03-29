package com.ssafy.house.api.member.dto.internal;

/**
 * 회원 계정의 repository 전용 조회 모델이다.
 *
 * @param mno 회원 번호
 * @param name 회원 이름
 * @param email 회원 이메일
 * @param password 암호화된 비밀번호
 */
public record MemberAccount(
        int mno,
        String name,
        String email,
        String password) {
}

