package com.ssafy.house.api.community.dto.request;

/**
 * 게시글 생성 및 수정 요청이다.
 *
 * @param categoryId 카테고리 식별자
 * @param title 제목
 * @param content 내용
 */
public record CommunityPostRequest(
        String categoryId,
        String title,
        String content) {
}
