package com.ssafy.house.api.community.dto.response;

import java.util.List;

/**
 * 게시글 페이지 응답이다.
 *
 * @param posts 게시글 목록
 * @param totalPages 전체 페이지 수
 */
public record CommunityPostPageResponse(
        List<CommunityPostSummaryResponse> posts,
        int totalPages) {
}
