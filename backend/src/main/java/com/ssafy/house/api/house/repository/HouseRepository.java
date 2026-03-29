package com.ssafy.house.api.house.repository;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ssafy.house.api.house.dto.internal.HouseDealDoneView;
import com.ssafy.house.api.house.dto.internal.HouseDealView;
import com.ssafy.house.api.house.dto.internal.HouseDetailView;
import com.ssafy.house.api.house.dto.internal.HouseInfoView;
import com.ssafy.house.api.house.dto.internal.HouseSimpleView;
import com.ssafy.house.api.house.dto.internal.SchoolInfoView;
import com.ssafy.house.api.house.entity.HouseDealDoneEntity;
import com.ssafy.house.api.house.entity.HouseInfoEntity;

import lombok.RequiredArgsConstructor;

/**
 * 아파트 도메인의 JPA 및 query repository를 조합하는 facade repository이다.
 */
@Repository
@RequiredArgsConstructor
public class HouseRepository {

    private final HouseInfoJpaRepository houseInfoJpaRepository;
    private final HouseDealDoneJpaRepository houseDealDoneJpaRepository;
    private final HouseInfoQueryRepository houseInfoQueryRepository;
    private final HouseDealQueryRepository houseDealQueryRepository;
    private final HouseSchoolQueryRepository houseSchoolQueryRepository;

    /**
     * 아파트 기본 정보를 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 아파트 기본 정보
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public HouseInfoView getHouseInfo(String aptSeq) throws SQLException {
        return houseInfoJpaRepository.findById(aptSeq)
                .map(this::mapToHouseInfoView)
                .orElse(null);
    }

    /**
     * 지도 범위 내 아파트 목록을 조회한다.
     *
     * @param minLat 최소 위도
     * @param maxLat 최대 위도
     * @param minLng 최소 경도
     * @param maxLng 최대 경도
     * @return 아파트 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public List<HouseInfoView> getHouseInfoByBounds(String minLat, String maxLat, String minLng, String maxLng)
            throws SQLException {
        return houseInfoQueryRepository.findByBounds(minLat, maxLat, minLng, maxLng);
    }

    /**
     * 아파트 식별자 목록으로 간략 정보를 조회한다.
     *
     * @param aptSeqList 아파트 식별자 목록
     * @return 아파트 간략 정보 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public List<HouseSimpleView> getHouseInfoBySeqList(List<String> aptSeqList) throws SQLException {
        return houseInfoQueryRepository.findBySeqList(aptSeqList);
    }

    /**
     * 아파트 상세 정보를 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 아파트 상세 정보
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public HouseDetailView getHouseInfoFull(String aptSeq) throws SQLException {
        return houseInfoQueryRepository.findHouseDetail(aptSeq);
    }

    /**
     * 아파트 주변 학교 목록을 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 학교 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public List<SchoolInfoView> getSchoolsByAptSeq(String aptSeq) throws SQLException {
        return houseSchoolQueryRepository.findSchoolsByAptSeq(aptSeq);
    }

    /**
     * 아파트 거래 완료 이력을 조회한다.
     *
     * @param aptSeq 아파트 식별자
     * @return 거래 완료 목록
     */
    public List<HouseDealDoneView> getDealsByAptSeq(String aptSeq) {
        return houseDealDoneJpaRepository.findAllByAptSeqOrderByDealYearAscDealMonthAscDealDayAsc(aptSeq).stream()
                .map(this::mapToHouseDealDoneView)
                .toList();
    }

    /**
     * 조건 기반 아파트 필터 검색을 수행한다.
     *
     * @param sido 시도명
     * @param gugun 구군명
     * @param dong 동명
     * @param aptNm 아파트명
     * @return 아파트 간략 정보 목록
     */
    public List<HouseSimpleView> searchFilter(String sido, String gugun, String dong, String aptNm) {
        return houseInfoQueryRepository.searchFilter(sido, gugun, dong, aptNm);
    }

    /**
     * 아파트명 일부로 아파트 식별자 목록을 조회한다.
     *
     * @param partialName 아파트명 일부
     * @return 아파트 식별자 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public List<String> searchByAptName(String partialName) throws SQLException {
        return houseInfoQueryRepository.searchByAptName(partialName);
    }

    /**
     * 옵션과 아파트명으로 아파트 식별자 목록을 조회한다.
     *
     * @param sido 시도명 일부
     * @param gugun 구군명 일부
     * @param dong 동명 일부
     * @param aptNm 아파트명 일부
     * @return 아파트 식별자 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public List<String> findByOptionsAndAptName(String sido, String gugun, String dong, String aptNm)
            throws SQLException {
        return houseInfoQueryRepository.findByOptionsAndAptName(sido, gugun, dong, aptNm);
    }

    /**
     * 옵션과 거래유형으로 매물 목록을 조회한다.
     *
     * @param sido 시도명 일부
     * @param gugun 구군명 일부
     * @param dong 동명 일부
     * @param aptNm 아파트명 일부
     * @param tradeType 거래유형
     * @return 매물 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public List<HouseDealView> findDealsByOptionsAndType(String sido, String gugun, String dong, String aptNm,
            String tradeType) throws SQLException {
        return houseDealQueryRepository.findDealsByOptionsAndType(sido, gugun, dong, aptNm, tradeType);
    }

    /**
     * 예산 이하의 매매 또는 전세 매물을 조회한다.
     *
     * @param sido 시도명 일부
     * @param gugun 구군명 일부
     * @param dong 동명 일부
     * @param aptNm 아파트명 일부
     * @param maxPrice 최대 가격
     * @return 매물 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public List<HouseDealView> findDealsByBudget(String sido, String gugun, String dong, String aptNm, long maxPrice)
            throws SQLException {
        return houseDealQueryRepository.findDealsByBudget(sido, gugun, dong, aptNm, maxPrice);
    }

    /**
     * 월세 한도 조건으로 월세 매물을 조회한다.
     *
     * @param sido 시도명 일부
     * @param gugun 구군명 일부
     * @param dong 동명 일부
     * @param aptNm 아파트명 일부
     * @param maxDeposit 최대 보증금
     * @param maxRent 최대 월세
     * @return 매물 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public List<HouseDealView> findRentDeals(String sido, String gugun, String dong, String aptNm, long maxDeposit,
            int maxRent) throws SQLException {
        return houseDealQueryRepository.findRentDeals(sido, gugun, dong, aptNm, maxDeposit, maxRent);
    }

    /**
     * 조건에 맞는 최저가 매물을 조회한다.
     *
     * @param sido 시도명 일부
     * @param gugun 구군명 일부
     * @param dong 동명 일부
     * @param aptNm 아파트명 일부
     * @param tradeType 거래유형
     * @return 최저가 매물
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public HouseDealView findLowestDeal(String sido, String gugun, String dong, String aptNm, String tradeType)
            throws SQLException {
        return houseDealQueryRepository.findLowestDeal(sido, gugun, dong, aptNm, tradeType);
    }

    /**
     * 조건에 맞는 가격 범위 매물을 조회한다.
     *
     * @param sido 시도명 일부
     * @param gugun 구군명 일부
     * @param dong 동명 일부
     * @param aptNm 아파트명 일부
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 매물 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public List<HouseDealView> findDealsByPriceRange(String sido, String gugun, String dong, String aptNm,
            long minPrice, long maxPrice) throws SQLException {
        return houseDealQueryRepository.findDealsByPriceRange(sido, gugun, dong, aptNm, minPrice, maxPrice);
    }

    /**
     * 조건에 맞는 최고가 매물을 조회한다.
     *
     * @param sido 시도명 일부
     * @param gugun 구군명 일부
     * @param dong 동명 일부
     * @param aptNm 아파트명 일부
     * @param tradeType 거래유형
     * @return 최고가 매물
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    public HouseDealView findHighestDeal(String sido, String gugun, String dong, String aptNm, String tradeType)
            throws SQLException {
        return houseDealQueryRepository.findHighestDeal(sido, gugun, dong, aptNm, tradeType);
    }

    private HouseInfoView mapToHouseInfoView(HouseInfoEntity houseInfo) {
        return new HouseInfoView(
                houseInfo.getAptSeq(),
                houseInfo.getSggCd(),
                houseInfo.getUmdCd(),
                houseInfo.getUmdNm(),
                houseInfo.getJibun(),
                houseInfo.getRoadNmSggCd(),
                houseInfo.getRoadNm(),
                houseInfo.getRoadNmBonbun(),
                houseInfo.getRoadNmBubun(),
                houseInfo.getAptNm(),
                houseInfo.getBuildYear(),
                houseInfo.getLatitude(),
                houseInfo.getLongitude());
    }

    private HouseDealDoneView mapToHouseDealDoneView(HouseDealDoneEntity deal) {
        return new HouseDealDoneView(
                deal.getNo(),
                deal.getAptSeq(),
                deal.getAptDong(),
                deal.getFloor(),
                deal.getDealYear(),
                deal.getDealMonth(),
                deal.getDealDay(),
                deal.getExcluUseAr(),
                deal.getDealAmount());
    }
}
