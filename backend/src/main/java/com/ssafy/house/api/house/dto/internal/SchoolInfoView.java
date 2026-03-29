package com.ssafy.house.api.house.dto.internal;

/**
 * 주변 학교 정보의 repository 전용 조회 모델이다.
 *
 * @param schoolName 학교명
 * @param schoolType 학교 유형
 * @param distance 거리
 */
public record SchoolInfoView(
        String schoolName,
        String schoolType,
        String distance) {
}

