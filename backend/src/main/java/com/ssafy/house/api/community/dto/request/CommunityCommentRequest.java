package com.ssafy.house.api.community.dto.request;

/**
 * 댓글 생성 및 수정 요청이다.
 *
 * @param content 댓글 내용
 */
public record CommunityCommentRequest(
        String content) {
}
