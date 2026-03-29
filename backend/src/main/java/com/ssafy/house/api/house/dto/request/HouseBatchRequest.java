package com.ssafy.house.api.house.dto.request;

import java.util.List;

/**
 * 아파트 식별자 목록 조회 요청이다.
 *
 * @param aptSeqList 아파트 식별자 목록
 */
public record HouseBatchRequest(
        List<String> aptSeqList) {
}
