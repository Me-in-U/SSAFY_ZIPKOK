package com.ssafy.house.api.house.dto.response;

/**
 * 주변 학교 정보 응답이다.
 *
 * @param schoolName 학교명
 * @param schoolType 학교 유형
 * @param distance 거리
 */
public record SchoolInfoResponse(
        String schoolName,
        String schoolType,
        String distance) {
}
