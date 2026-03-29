package com.ssafy.house.api.member.dto.response;

import java.util.List;

/**
 * 내 즐겨찾기 목록 응답이다.
 *
 * @param favorites 즐겨찾기 목록
 */
public record MemberFavoriteListResponse(
        List<FavoriteSummaryResponse> favorites) {
}
