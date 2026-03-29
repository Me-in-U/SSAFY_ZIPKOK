package com.ssafy.house.api.recommend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.api.recommend.dto.response.RecommendPropertyListResponse;
import com.ssafy.house.api.recommend.service.RecommendQueryService;
import com.ssafy.house.global.common.base.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 신규 추천 매물 API를 제공하는 컨트롤러이다.
 */
@RestController
@RequestMapping("/v1/recommend")
@RequiredArgsConstructor
@Tag(name = "Recommend", description = "추천 매물 조회 기능")
public class RecommendController {

    private final RecommendQueryService recommendQueryService;

    /**
     * 최근 거래 추천 매물을 조회한다.
     *
     * @param limit 조회 건수
     * @return 추천 매물 목록 응답
     */
    @GetMapping("/recent")
    @Operation(summary = "최근 거래 추천 조회", description = "최근 거래 기준 추천 매물 목록을 조회한다.")
    public BaseResponse<RecommendPropertyListResponse> getRecentProperties(
            @RequestParam(defaultValue = "6") int limit) {
        return BaseResponse.onSuccess(recommendQueryService.getRecentProperties(limit));
    }

    /**
     * 역세권 추천 매물을 조회한다.
     *
     * @param limit 조회 건수
     * @return 추천 매물 목록 응답
     */
    @GetMapping("/nearstation")
    @Operation(summary = "역세권 추천 조회", description = "역세권 기준 추천 매물 목록을 조회한다.")
    public BaseResponse<RecommendPropertyListResponse> getNearStationProperties(
            @RequestParam(defaultValue = "6") int limit) {
        return BaseResponse.onSuccess(recommendQueryService.getNearStationProperties(limit));
    }

    /**
     * 신혼부부 추천 매물을 조회한다.
     *
     * @param limit 조회 건수
     * @return 추천 매물 목록 응답
     */
    @GetMapping("/newlyweds")
    @Operation(summary = "신혼부부 추천 조회", description = "신혼부부 기준 추천 매물 목록을 조회한다.")
    public BaseResponse<RecommendPropertyListResponse> getNewlywedsProperties(
            @RequestParam(defaultValue = "6") int limit) {
        return BaseResponse.onSuccess(recommendQueryService.getNewlywedsProperties(limit));
    }
}
