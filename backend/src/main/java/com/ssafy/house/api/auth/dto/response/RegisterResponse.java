package com.ssafy.house.api.auth.dto.response;

import com.ssafy.house.api.member.dto.response.MemberProfileResponse;

/**
 * 회원가입 성공 응답 데이터이다.
 *
 * @param member 생성된 회원 정보
 */
public record RegisterResponse(
        MemberProfileResponse member) {
}
