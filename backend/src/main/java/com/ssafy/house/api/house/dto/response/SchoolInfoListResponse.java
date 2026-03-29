package com.ssafy.house.api.house.dto.response;

import java.util.List;

/**
 * 주변 학교 목록 응답이다.
 *
 * @param schools 학교 목록
 */
public record SchoolInfoListResponse(
        List<SchoolInfoResponse> schools) {
}
