package com.ssafy.house.api.house.dto.internal;

/**
 * 매물 정보의 repository 전용 조회 모델이다.
 *
 * @param dealId 거래 식별자
 * @param aptSeq 아파트 식별자
 * @param listingName 단지명
 * @param dealType 거래 유형
 * @param price 가격
 * @param propertyType 매물 유형
 * @param spec 면적 등 요약 정보
 * @param description 설명
 * @param confirmedAt 확인 시각
 * @param deposit 보증금
 * @param monthlyRent 월세
 */
public record HouseDealView(
        Integer dealId,
        String aptSeq,
        String listingName,
        String dealType,
        long price,
        String propertyType,
        String spec,
        String description,
        String confirmedAt,
        long deposit,
        Integer monthlyRent) {
}

