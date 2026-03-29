package com.ssafy.house.api.house.dto.response;

/**
 * 아파트 상세 정보 응답이다.
 *
 * @param aptSeq 아파트 식별자
 * @param aptNm 아파트명
 * @param latitude 위도
 * @param longitude 경도
 * @param roadAddress 도로명 주소
 * @param jibunAddress 지번 주소
 * @param buildYear 준공 연도
 * @param areaMin 최소 면적
 * @param areaMax 최대 면적
 * @param tradePriceMin 최소 매매가
 * @param tradePriceMax 최대 매매가
 * @param jeonsePriceMin 최소 전세가
 * @param jeonsePriceMax 최대 전세가
 * @param lastTradeDetail 최근 거래 상세
 * @param imgPath 이미지 경로
 * @param dealType 최근 거래 유형
 * @param deposit 보증금
 * @param monthlyRent 월세
 * @param latestPrice 최근 가격
 * @param latestSpec 최근 면적 요약
 * @param propertyType 매물 유형
 * @param description 설명
 */
public record HouseDetailResponse(
        String aptSeq,
        String aptNm,
        String latitude,
        String longitude,
        String roadAddress,
        String jibunAddress,
        Integer buildYear,
        Double areaMin,
        Double areaMax,
        Long tradePriceMin,
        Long tradePriceMax,
        Long jeonsePriceMin,
        Long jeonsePriceMax,
        String lastTradeDetail,
        String imgPath,
        String dealType,
        Long deposit,
        Long monthlyRent,
        Long latestPrice,
        String latestSpec,
        String propertyType,
        String description) {
}
