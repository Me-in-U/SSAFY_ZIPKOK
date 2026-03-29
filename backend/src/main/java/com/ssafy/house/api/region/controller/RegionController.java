package com.ssafy.house.api.region.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.api.region.dto.response.RegionNameListResponse;
import com.ssafy.house.api.region.service.RegionQueryService;
import com.ssafy.house.global.common.base.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 신규 행정구역 조회 API를 제공하는 컨트롤러이다.
 */
@RestController
@RequestMapping("/v1/region")
@RequiredArgsConstructor
@Tag(name = "Region", description = "행정구역 조회 기능")
public class RegionController {

    private final RegionQueryService regionQueryService;

    /**
     * 시도 목록을 조회한다.
     *
     * @return 시도 목록 응답
     */
    @GetMapping("/sidos")
    @Operation(summary = "시도 목록 조회", description = "전체 시도 목록을 조회한다.")
    public BaseResponse<RegionNameListResponse> getSidos() {
        return BaseResponse.onSuccess(regionQueryService.getSidos());
    }

    /**
     * 특정 시도의 구군 목록을 조회한다.
     *
     * @param sidoName 시도명
     * @return 구군 목록 응답
     */
    @GetMapping("/sidos/{sidoName}/guguns")
    @Operation(summary = "구군 목록 조회", description = "특정 시도의 구군 목록을 조회한다.")
    public BaseResponse<RegionNameListResponse> getGuguns(@PathVariable String sidoName) {
        return BaseResponse.onSuccess(regionQueryService.getGuguns(sidoName));
    }

    /**
     * 특정 시도와 구군의 동 목록을 조회한다.
     *
     * @param sidoName 시도명
     * @param gugunName 구군명
     * @return 동 목록 응답
     */
    @GetMapping("/sidos/{sidoName}/guguns/{gugunName}/dongs")
    @Operation(summary = "동 목록 조회", description = "특정 시도와 구군의 동 목록을 조회한다.")
    public BaseResponse<RegionNameListResponse> getDongs(
            @PathVariable String sidoName,
            @PathVariable String gugunName) {
        return BaseResponse.onSuccess(regionQueryService.getDongs(sidoName, gugunName));
    }
}
