package com.ssafy.house.api.auth.dto.response;

import com.ssafy.house.api.member.dto.response.MemberProfileResponse;

/**
 * 로그인 성공 응답 데이터이다.
 *
 * @param token JWT 토큰
 * @param user 로그인한 회원 정보
 */
public record LoginResponse(
        String token,
        MemberProfileResponse user) {
}
