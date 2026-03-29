package com.ssafy.house.api.member.dto.response;

/**
 * 즐겨찾기 추가 또는 삭제 응답이다.
 *
 * @param aptSeq 처리한 아파트 식별자
 */
public record MemberFavoriteCommandResponse(
        String aptSeq) {
}
