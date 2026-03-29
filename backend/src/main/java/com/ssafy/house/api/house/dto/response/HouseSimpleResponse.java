package com.ssafy.house.api.house.dto.response;

/**
 * 아파트 간략 정보 응답이다.
 *
 * @param aptSeq 아파트 식별자
 * @param aptNm 아파트명
 * @param latitude 위도
 * @param longitude 경도
 */
public record HouseSimpleResponse(
        String aptSeq,
        String aptNm,
        String latitude,
        String longitude) {
}
