package com.ssafy.house.api.member.dto.response;

/**
 * 즐겨찾기 아파트 요약 응답이다.
 *
 * @param aptSeq 아파트 식별자
 * @param imgPath 이미지 경로
 * @param listingName 단지명
 * @param price 가격
 * @param spec 면적 등 요약 정보
 * @param propertyType 매물 유형
 * @param description 설명
 */
public record FavoriteSummaryResponse(
        String aptSeq,
        String imgPath,
        String listingName,
        Long price,
        String spec,
        String propertyType,
        String description) {
}
