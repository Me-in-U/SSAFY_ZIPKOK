package com.ssafy.house.api.news.repository;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ssafy.house.api.news.dto.internal.NewsArticle;

import lombok.RequiredArgsConstructor;

/**
 * 뉴스 도메인의 JDBC 조회를 담당하는 repository이다.
 */
@Repository
@RequiredArgsConstructor
public class NewsRepository {

    private final JdbcClient jdbcClient;

    /**
     * 최신 뉴스 목록을 조회한다.
     *
     * @param limit 조회 건수
     * @param offset 조회 시작 위치
     * @return 뉴스 목록
     */
    public List<NewsArticle> getLatest(int limit, int offset) {
        return jdbcClient.sql("""
                SELECT
                    id,
                    title,
                    originallink AS originalLink,
                    naverlink AS naverLink,
                    description,
                    pub_date AS publishedAt
                FROM real_estate_news
                ORDER BY pub_date DESC
                LIMIT :limit
                OFFSET :offset
                """)
                .param("limit", limit)
                .param("offset", offset)
                .query(NewsArticle.class)
                .list();
    }
}


