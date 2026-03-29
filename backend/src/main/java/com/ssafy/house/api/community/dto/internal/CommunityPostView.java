package com.ssafy.house.api.community.dto.internal;

import java.sql.Timestamp;
import java.util.List;

/**
 * 게시글의 repository 전용 조회 모델이다.
 *
 * @param postId 게시글 식별자
 * @param categoryId 카테고리 식별자
 * @param authorId 작성자 회원 번호
 * @param authorName 작성자 이름
 * @param title 제목
 * @param content 내용
 * @param createdAt 작성 시각
 * @param views 조회수
 * @param commentCount 댓글 수
 * @param comments 댓글 목록
 */
public record CommunityPostView(
        int postId,
        String categoryId,
        int authorId,
        String authorName,
        String title,
        String content,
        Timestamp createdAt,
        int views,
        int commentCount,
        List<CommunityCommentView> comments) {
}

