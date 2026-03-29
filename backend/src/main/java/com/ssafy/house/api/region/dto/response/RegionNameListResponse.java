package com.ssafy.house.api.region.dto.response;

import java.util.List;

/**
 * 행정구역 이름 목록 응답이다.
 *
 * @param items 행정구역 이름 목록
 */
public record RegionNameListResponse(
        List<String> items) {
}
