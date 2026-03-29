package com.ssafy.house.api.house.dto.internal;

/**
 * 아파트 간략 정보의 repository 전용 조회 모델이다.
 *
 * @param aptSeq 아파트 식별자
 * @param aptNm 아파트명
 * @param latitude 위도
 * @param longitude 경도
 */
public record HouseSimpleView(
        String aptSeq,
        String aptNm,
        String latitude,
        String longitude) {
}

