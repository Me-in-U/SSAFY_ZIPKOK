package com.ssafy.house.api.community.dto.internal;

import java.util.List;

/**
 * 게시글 페이지의 repository 전용 조회 모델이다.
 *
 * @param posts 게시글 목록
 * @param totalPages 전체 페이지 수
 */
public record CommunityPostPageResult(
        List<CommunityPostView> posts,
        int totalPages) {
}

