package com.ssafy.house.api.member.dto.internal;

/**
 * 회원 즐겨찾기 아파트의 repository 전용 조회 모델이다.
 *
 * @param aptSeq 아파트 식별자
 * @param imgPath 이미지 경로
 * @param listingName 단지명
 * @param price 가격
 * @param spec 면적 등 요약 정보
 * @param propertyType 매물 유형
 * @param description 설명
 */
public record MemberFavoriteSummary(
        String aptSeq,
        String imgPath,
        String listingName,
        Long price,
        String spec,
        String propertyType,
        String description) {
}

