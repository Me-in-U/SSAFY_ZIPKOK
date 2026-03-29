package com.ssafy.house.api.house.dto.response;

import java.util.List;

/**
 * 아파트 기본 정보 목록 응답이다.
 *
 * @param houses 아파트 목록
 */
public record HouseInfoListResponse(
        List<HouseInfoResponse> houses) {
}
