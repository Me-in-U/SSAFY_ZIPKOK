package com.ssafy.house.api.recommend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.house.api.recommend.repository.RecommendRepository;
import com.ssafy.house.api.recommend.dto.response.RecommendPropertyListResponse;
import com.ssafy.house.api.recommend.dto.response.RecommendPropertyResponse;
import com.ssafy.house.api.recommend.dto.internal.RecommendProperty;

import lombok.RequiredArgsConstructor;

/**
 * 추천 매물 조회 응답을 신규 API 구조에 맞게 제공하는 서비스이다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendQueryService {

    private final RecommendRepository recommendRepository;

    /**
     * 최근 거래 추천 매물을 조회한다.
     *
     * @param limit 조회 건수
     * @return 추천 매물 목록 응답
     */
    public RecommendPropertyListResponse getRecentProperties(int limit) {
        return new RecommendPropertyListResponse(mapToResponses(recommendRepository.getRecentProperties(limit)));
    }

    /**
     * 역세권 추천 매물을 조회한다.
     *
     * @param limit 조회 건수
     * @return 추천 매물 목록 응답
     */
    public RecommendPropertyListResponse getNearStationProperties(int limit) {
        return new RecommendPropertyListResponse(
                mapToResponses(recommendRepository.getNearStationProperties(limit)));
    }

    /**
     * 신혼부부 추천 매물을 조회한다.
     *
     * @param limit 조회 건수
     * @return 추천 매물 목록 응답
     */
    public RecommendPropertyListResponse getNewlywedsProperties(int limit) {
        return new RecommendPropertyListResponse(
                mapToResponses(recommendRepository.getNewlywedsProperties(limit)));
    }

    private List<RecommendPropertyResponse> mapToResponses(List<RecommendProperty> properties) {
        return properties.stream()
                .map(property -> new RecommendPropertyResponse(
                        property.aptSeq(),
                        property.imgPath(),
                        property.listingName(),
                        property.price(),
                        property.spec(),
                        property.propertyType(),
                        property.description()))
                .toList();
    }
}

