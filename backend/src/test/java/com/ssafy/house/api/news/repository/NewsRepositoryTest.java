package com.ssafy.house.api.news.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.ssafy.house.api.news.dto.internal.NewsArticle;

@ExtendWith(MockitoExtension.class)
class NewsRepositoryTest {

    @Mock
    private JdbcClient jdbcClient;

    @Mock
    private JdbcClient.StatementSpec statementSpec;

    @Mock
    private JdbcClient.MappedQuerySpec<NewsArticle> mappedQuerySpec;

    @InjectMocks
    private NewsRepository newsRepository;

    @Test
    void getLatest_usesJdbcClientQuery() {
        List<NewsArticle> expected = List.of(new NewsArticle(
                1,
                "제목",
                "https://example.com/original",
                "https://example.com/naver",
                "설명",
                LocalDateTime.of(2026, 3, 29, 23, 30)));
        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("limit", 5)).thenReturn(statementSpec);
        when(statementSpec.param("offset", 2)).thenReturn(statementSpec);
        when(statementSpec.query(NewsArticle.class)).thenReturn(mappedQuerySpec);
        when(mappedQuerySpec.list()).thenReturn(expected);

        assertThat(newsRepository.getLatest(5, 2)).isEqualTo(expected);
    }
}
