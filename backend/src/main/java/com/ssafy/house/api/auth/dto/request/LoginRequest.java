package com.ssafy.house.api.auth.dto.request;

/**
 * 로그인 요청 데이터이다.
 *
 * @param email 회원 이메일
 * @param password 회원 비밀번호
 */
public record LoginRequest(
        String email,
        String password) {
}
