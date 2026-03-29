package com.ssafy.house.api.house.dto.response;

import java.util.List;

/**
 * 거래 완료 이력 목록 응답이다.
 *
 * @param deals 거래 이력 목록
 */
public record HouseDealDoneListResponse(
        List<HouseDealDoneResponse> deals) {
}
