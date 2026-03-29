package com.ssafy.house.api.recommend.dto.response;

import java.util.List;

/**
 * 추천 매물 목록 응답이다.
 *
 * @param properties 추천 매물 목록
 */
public record RecommendPropertyListResponse(
        List<RecommendPropertyResponse> properties) {
}
