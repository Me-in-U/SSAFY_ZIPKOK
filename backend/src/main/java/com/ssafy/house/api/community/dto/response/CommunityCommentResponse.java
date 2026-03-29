package com.ssafy.house.api.community.dto.response;

import java.sql.Timestamp;

/**
 * 댓글 응답이다.
 *
 * @param commentId 댓글 식별자
 * @param postId 게시글 식별자
 * @param authorId 작성자 회원 번호
 * @param authorName 작성자 이름
 * @param content 댓글 내용
 * @param createdAt 작성 시각
 */
public record CommunityCommentResponse(
        int commentId,
        int postId,
        int authorId,
        String authorName,
        String content,
        Timestamp createdAt) {
}
