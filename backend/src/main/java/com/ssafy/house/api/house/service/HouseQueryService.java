package com.ssafy.house.api.house.service;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.house.api.house.repository.HouseRepository;
import com.ssafy.house.api.house.dto.internal.HouseDealDoneView;
import com.ssafy.house.api.house.dto.internal.HouseDetailView;
import com.ssafy.house.api.house.dto.internal.HouseInfoView;
import com.ssafy.house.api.house.dto.internal.HouseSimpleView;
import com.ssafy.house.api.house.dto.internal.SchoolInfoView;
import com.ssafy.house.api.house.dto.response.HouseDealDoneListResponse;
import com.ssafy.house.api.house.dto.response.HouseDealDoneResponse;
import com.ssafy.house.api.house.dto.response.HouseDetailResponse;
import com.ssafy.house.api.house.dto.response.HouseInfoListResponse;
import com.ssafy.house.api.house.dto.response.HouseInfoResponse;
import com.ssafy.house.api.house.dto.response.HouseSimpleListResponse;
import com.ssafy.house.api.house.dto.response.HouseSimpleResponse;
import com.ssafy.house.api.house.dto.response.SchoolInfoListResponse;
import com.ssafy.house.api.house.dto.response.SchoolInfoResponse;

import lombok.RequiredArgsConstructor;

/**
 * 아파트 조회 응답을 신규 API 구조에 맞게 제공하는 서비스이다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HouseQueryService {

    private final HouseRepository houseRepository;

    /**
     * 아파트 기본 정보를 단건 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 아파트 기본 정보
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public HouseInfoResponse getHouse(String aptSeq) throws SQLException {
        HouseInfoView houseInfo = houseRepository.getHouseInfo(aptSeq);
        if (houseInfo == null) {
            throw new NoSuchElementException("아파트 정보를 찾을 수 없습니다.");
        }
        return mapToHouseInfoResponse(houseInfo);
    }

    /**
     * 지도 범위 내 아파트 목록을 조회한다.
     *
     * @param minLat 최소 위도
     * @param maxLat 최대 위도
     * @param minLng 최소 경도
     * @param maxLng 최대 경도
     * @return 아파트 목록 응답
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public HouseInfoListResponse getHousesByBounds(String minLat, String maxLat, String minLng, String maxLng)
            throws SQLException {
        return new HouseInfoListResponse(houseRepository.getHouseInfoByBounds(minLat, maxLat, minLng, maxLng)
                .stream()
                .map(this::mapToHouseInfoResponse)
                .toList());
    }

    /**
     * 아파트 식별자 목록으로 간략 정보를 조회한다.
     *
     * @param aptSeqList 아파트 식별자 목록
     * @return 아파트 간략 정보 목록 응답
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public HouseSimpleListResponse getHousesBySeqList(List<String> aptSeqList) throws SQLException {
        return new HouseSimpleListResponse(houseRepository.getHouseInfoBySeqList(aptSeqList).stream()
                .map(this::mapToHouseSimpleResponse)
                .toList());
    }

    /**
     * 아파트 상세 정보를 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 아파트 상세 정보
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public HouseDetailResponse getHouseDetail(String aptSeq) throws SQLException {
        HouseDetailView detail = houseRepository.getHouseInfoFull(aptSeq);
        if (detail == null) {
            throw new NoSuchElementException("아파트 상세 정보를 찾을 수 없습니다.");
        }
        return mapToHouseDetailResponse(detail);
    }

    /**
     * 특정 아파트 주변 학교 목록을 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 학교 목록 응답
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public SchoolInfoListResponse getSchools(String aptSeq) throws SQLException {
        return new SchoolInfoListResponse(houseRepository.getSchoolsByAptSeq(aptSeq).stream()
                .map(this::mapToSchoolInfoResponse)
                .toList());
    }

    /**
     * 특정 아파트의 거래 완료 이력을 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 거래 완료 이력 응답
     */
    public HouseDealDoneListResponse getDeals(String aptSeq) {
        return new HouseDealDoneListResponse(houseRepository.getDealsByAptSeq(aptSeq).stream()
                .map(this::mapToHouseDealDoneResponse)
                .toList());
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
    public HouseSimpleListResponse searchByFilter(String sido, String gugun, String dong, String aptNm) {
        return new HouseSimpleListResponse(houseRepository.searchFilter(sido, gugun, dong, aptNm).stream()
                .map(this::mapToHouseSimpleResponse)
                .toList());
    }

    private HouseInfoResponse mapToHouseInfoResponse(HouseInfoView houseInfo) {
        return new HouseInfoResponse(
                houseInfo.aptSeq(),
                houseInfo.sggCd(),
                houseInfo.umdCd(),
                houseInfo.umdNm(),
                houseInfo.jibun(),
                houseInfo.roadNmSggCd(),
                houseInfo.roadNm(),
                houseInfo.roadNmBonbun(),
                houseInfo.roadNmBubun(),
                houseInfo.aptNm(),
                houseInfo.buildYear(),
                houseInfo.latitude(),
                houseInfo.longitude());
    }

    private HouseSimpleResponse mapToHouseSimpleResponse(HouseSimpleView houseInfo) {
        return new HouseSimpleResponse(
                houseInfo.aptSeq(),
                houseInfo.aptNm(),
                houseInfo.latitude(),
                houseInfo.longitude());
    }

    private HouseDetailResponse mapToHouseDetailResponse(HouseDetailView detail) {
        return new HouseDetailResponse(
                detail.aptSeq(),
                detail.aptNm(),
                detail.latitude(),
                detail.longitude(),
                detail.roadAddress(),
                detail.jibunAddress(),
                detail.buildYear(),
                detail.areaMin(),
                detail.areaMax(),
                detail.tradePriceMin(),
                detail.tradePriceMax(),
                detail.jeonsePriceMin(),
                detail.jeonsePriceMax(),
                detail.lastTradeDetail(),
                detail.imgPath(),
                detail.dealType(),
                detail.deposit(),
                detail.monthlyRent(),
                detail.latestPrice(),
                detail.latestSpec(),
                detail.propertyType(),
                detail.description());
    }

    private SchoolInfoResponse mapToSchoolInfoResponse(SchoolInfoView schoolInfo) {
        return new SchoolInfoResponse(
                schoolInfo.schoolName(),
                schoolInfo.schoolType(),
                schoolInfo.distance());
    }

    private HouseDealDoneResponse mapToHouseDealDoneResponse(HouseDealDoneView deal) {
        return new HouseDealDoneResponse(
                deal.no(),
                deal.aptSeq(),
                deal.aptDong(),
                deal.floor(),
                deal.dealYear(),
                deal.dealMonth(),
                deal.dealDay(),
                deal.excluUseAr(),
                deal.dealAmount());
    }
}

