package com.ssafy.house.api.news.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.house.api.news.repository.NewsRepository;
import com.ssafy.house.api.news.dto.response.NewsLatestResponse;
import com.ssafy.house.api.news.dto.internal.NewsArticle;

@ExtendWith(MockitoExtension.class)
class NewsQueryServiceTest {

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private NewsQueryService newsQueryService;

    @Test
    void getLatestNews_mapsLegacyNewsToResponse() {
        LocalDateTime publishedAt = LocalDateTime.of(2026, 3, 29, 12, 30);
        NewsArticle news = new NewsArticle(
                1,
                "최신 부동산 뉴스",
                "https://example.com/original",
                "https://example.com/naver",
                "요약",
                publishedAt);
        when(newsRepository.getLatest(5, 10)).thenReturn(List.of(news));

        NewsLatestResponse response = newsQueryService.getLatestNews(5, 10);

        assertThat(response.news()).hasSize(1);
        assertThat(response.news().getFirst().title()).isEqualTo("최신 부동산 뉴스");
        assertThat(response.news().getFirst().originalLink()).isEqualTo("https://example.com/original");
        assertThat(response.news().getFirst().naverLink()).isEqualTo("https://example.com/naver");
        assertThat(response.news().getFirst().publishedAt()).isEqualTo(publishedAt);
    }
}

