package com.ssafy.house.api.region.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.house.api.region.repository.RegionRepository;
import com.ssafy.house.api.region.dto.response.RegionNameListResponse;

import lombok.RequiredArgsConstructor;

/**
 * 행정구역 조회 응답을 신규 API 구조에 맞게 제공하는 서비스이다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegionQueryService {

    private final RegionRepository regionRepository;

    /**
     * 시도 목록을 조회한다.
     *
     * @return 시도 목록 응답
     */
    public RegionNameListResponse getSidos() {
        return new RegionNameListResponse(regionRepository.getAllSido());
    }

    /**
     * 특정 시도의 구군 목록을 조회한다.
     *
     * @param sidoName 시도명
     * @return 구군 목록 응답
     */
    public RegionNameListResponse getGuguns(String sidoName) {
        return new RegionNameListResponse(regionRepository.getGugunBySido(sidoName));
    }

    /**
     * 특정 시도와 구군의 동 목록을 조회한다.
     *
     * @param sidoName 시도명
     * @param gugunName 구군명
     * @return 동 목록 응답
     */
    public RegionNameListResponse getDongs(String sidoName, String gugunName) {
        return new RegionNameListResponse(regionRepository.getDongBySidoAndGugun(sidoName, gugunName));
    }
}
