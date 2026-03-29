package com.ssafy.house.api.news.dto.response;

import java.util.List;

/**
 * 최신 뉴스 목록 응답 데이터이다.
 *
 * @param news 최신 뉴스 목록
 */
public record NewsLatestResponse(List<NewsSummaryResponse> news) {
}
