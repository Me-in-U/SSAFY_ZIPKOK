package com.ssafy.house.api.news.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.house.api.news.repository.NewsRepository;
import com.ssafy.house.api.news.dto.response.NewsLatestResponse;
import com.ssafy.house.api.news.dto.response.NewsSummaryResponse;
import com.ssafy.house.api.news.dto.internal.NewsArticle;

import lombok.RequiredArgsConstructor;

/**
 * 뉴스 조회 응답을 신규 API 구조에 맞게 변환하는 서비스이다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewsQueryService {

    private final NewsRepository newsRepository;

    /**
     * 최신 뉴스 목록을 조회해 응답 DTO로 변환한다.
     *
     * @param limit 조회 건수
     * @param offset 조회 시작 위치
     * @return 최신 뉴스 응답
     */
    public NewsLatestResponse getLatestNews(int limit, int offset) {
        List<NewsSummaryResponse> news = newsRepository.getLatest(limit, offset).stream()
                .map(this::mapToResponse)
                .toList();
        return new NewsLatestResponse(news);
    }

    private NewsSummaryResponse mapToResponse(NewsArticle news) {
        return new NewsSummaryResponse(
                news.id(),
                news.title(),
                news.originalLink(),
                news.naverLink(),
                news.description(),
                news.publishedAt());
    }
}

