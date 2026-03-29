package com.ssafy.house.api.auth.dto.request;

/**
 * 회원가입 요청 데이터이다.
 *
 * @param name 회원 이름
 * @param email 회원 이메일
 * @param password 회원 비밀번호
 */
public record RegisterRequest(
        String name,
        String email,
        String password) {
}
