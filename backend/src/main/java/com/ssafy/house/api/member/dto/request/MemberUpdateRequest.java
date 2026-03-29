package com.ssafy.house.api.member.dto.request;

/**
 * 내 정보 수정 요청 데이터이다.
 *
 * @param name 변경할 이름
 * @param currentPassword 현재 비밀번호
 * @param newPassword 새 비밀번호
 */
public record MemberUpdateRequest(
        String name,
        String currentPassword,
        String newPassword) {
}
