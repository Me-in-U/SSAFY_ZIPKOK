package com.ssafy.house.api.member.dto.response;

import java.util.List;

/**
 * 회원 페이지 조회 응답이다.
 *
 * @param members 회원 목록
 * @param currentPage 현재 페이지 번호
 * @param itemsPerPage 페이지 크기
 * @param totalItems 전체 회원 수
 * @param totalPages 전체 페이지 수
 * @param hasPre 이전 페이지 존재 여부
 * @param hasNext 다음 페이지 존재 여부
 * @param startPage 시작 페이지 번호
 * @param endPage 끝 페이지 번호
 */
public record MemberPageResponse(
        List<MemberProfileResponse> members,
        int currentPage,
        int itemsPerPage,
        int totalItems,
        int totalPages,
        boolean hasPre,
        boolean hasNext,
        int startPage,
        int endPage) {
}
