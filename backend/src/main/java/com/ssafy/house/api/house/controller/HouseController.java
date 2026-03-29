package com.ssafy.house.api.house.controller;

import java.sql.SQLException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.api.house.dto.request.HouseBatchRequest;
import com.ssafy.house.api.house.dto.response.HouseDealDoneListResponse;
import com.ssafy.house.api.house.dto.response.HouseDetailResponse;
import com.ssafy.house.api.house.dto.response.HouseInfoListResponse;
import com.ssafy.house.api.house.dto.response.HouseInfoResponse;
import com.ssafy.house.api.house.dto.response.HouseSimpleListResponse;
import com.ssafy.house.api.house.dto.response.SchoolInfoListResponse;
import com.ssafy.house.api.house.service.HouseQueryService;
import com.ssafy.house.global.common.base.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 신규 아파트 조회 API를 제공하는 컨트롤러이다.
 */
@RestController
@RequestMapping("/v1/house")
@RequiredArgsConstructor
@Tag(name = "House", description = "아파트 조회 기능")
public class HouseController {

    private final HouseQueryService houseQueryService;

    /**
     * 아파트 기본 정보를 단건 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 아파트 기본 정보 응답
     */
    @GetMapping("/{aptSeq}")
    @Operation(summary = "아파트 단건 조회", description = "아파트 식별자로 기본 정보를 조회한다.")
    public BaseResponse<HouseInfoResponse> getHouse(@PathVariable String aptSeq) throws SQLException {
        return BaseResponse.onSuccess(houseQueryService.getHouse(aptSeq));
    }

    /**
     * 지도 범위 내 아파트 목록을 조회한다.
     *
     * @param minLat 최소 위도
     * @param maxLat 최대 위도
     * @param minLng 최소 경도
     * @param maxLng 최대 경도
     * @return 아파트 목록 응답
     */
    @GetMapping("/search")
    @Operation(summary = "아파트 범위 조회", description = "지도 범위 내 아파트 목록을 조회한다.")
    public BaseResponse<HouseInfoListResponse> getHousesByBounds(
            @RequestParam String minLat,
            @RequestParam String maxLat,
            @RequestParam String minLng,
            @RequestParam String maxLng) throws SQLException {
        return BaseResponse.onSuccess(houseQueryService.getHousesByBounds(minLat, maxLat, minLng, maxLng));
    }

    /**
     * 아파트 식별자 목록으로 간략 정보를 조회한다.
     *
     * @param request 아파트 식별자 목록 요청
     * @return 아파트 간략 정보 목록 응답
     */
    @PostMapping("/batch")
    @Operation(summary = "아파트 복수 조회", description = "아파트 식별자 목록으로 간략 정보를 조회한다.")
    public BaseResponse<HouseSimpleListResponse> getHousesBySeqList(@RequestBody HouseBatchRequest request)
            throws SQLException {
        return BaseResponse.onSuccess(houseQueryService.getHousesBySeqList(request.aptSeqList()));
    }

    /**
     * 아파트 상세 정보를 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 아파트 상세 정보 응답
     */
    @GetMapping("/{aptSeq}/detail")
    @Operation(summary = "아파트 상세 조회", description = "아파트 상세 정보와 최근 거래 요약을 조회한다.")
    public BaseResponse<HouseDetailResponse> getHouseDetail(@PathVariable String aptSeq)
            throws SQLException {
        return BaseResponse.onSuccess(houseQueryService.getHouseDetail(aptSeq));
    }

    /**
     * 아파트 주변 학교 목록을 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 학교 목록 응답
     */
    @GetMapping("/{aptSeq}/schools")
    @Operation(summary = "아파트 주변 학교 조회", description = "아파트 주변 학교 목록을 조회한다.")
    public BaseResponse<SchoolInfoListResponse> getSchools(@PathVariable String aptSeq) throws SQLException {
        return BaseResponse.onSuccess(houseQueryService.getSchools(aptSeq));
    }

    /**
     * 아파트 거래 완료 이력을 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 거래 완료 이력 응답
     */
    @GetMapping("/{aptSeq}/deals-done")
    @Operation(summary = "거래 완료 이력 조회", description = "아파트 거래 완료 이력을 조회한다.")
    public BaseResponse<HouseDealDoneListResponse> getDeals(@PathVariable String aptSeq) {
        return BaseResponse.onSuccess(houseQueryService.getDeals(aptSeq));
    }

    /**
     * 조건 기반 아파트 필터 검색을 수행한다.
     *
     * @param sido 시도명
     * @param gugun 구군명
     * @param dong 동명
     * @param aptNm 아파트명
     * @return 아파트 간략 정보 목록 응답
     */
    @GetMapping("/filter")
    @Operation(summary = "아파트 필터 검색", description = "조건 기반으로 아파트를 필터 검색한다.")
    public BaseResponse<HouseSimpleListResponse> searchByFilter(
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String gugun,
            @RequestParam(required = false) String dong,
            @RequestParam(required = false) String aptNm) {
        return BaseResponse.onSuccess(houseQueryService.searchByFilter(sido, gugun, dong, aptNm));
    }
}
