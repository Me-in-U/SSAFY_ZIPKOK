package com.ssafy.house.api.house.dto.response;

import java.math.BigDecimal;

/**
 * 거래 완료 이력 응답이다.
 *
 * @param no 거래 번호
 * @param aptSeq 아파트 식별자
 * @param aptDong 동 정보
 * @param floor 층수
 * @param dealYear 거래 연도
 * @param dealMonth 거래 월
 * @param dealDay 거래 일
 * @param excluUseAr 전용 면적
 * @param dealAmount 거래 금액
 */
public record HouseDealDoneResponse(
        Integer no,
        String aptSeq,
        String aptDong,
        String floor,
        Integer dealYear,
        Integer dealMonth,
        Integer dealDay,
        BigDecimal excluUseAr,
        String dealAmount) {
}
