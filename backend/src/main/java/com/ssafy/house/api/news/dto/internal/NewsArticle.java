package com.ssafy.house.api.news.dto.internal;

import java.time.LocalDateTime;

/**
 * 뉴스 조회의 repository 전용 모델이다.
 *
 * @param id 뉴스 식별자
 * @param title 뉴스 제목
 * @param originalLink 원문 링크
 * @param naverLink 네이버 링크
 * @param description 뉴스 요약
 * @param publishedAt 발행 시각
 */
public record NewsArticle(
        int id,
        String title,
        String originalLink,
        String naverLink,
        String description,
        LocalDateTime publishedAt) {
}

