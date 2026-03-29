package com.ssafy.house.api.ai.tool;

import java.sql.SQLException;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.ssafy.house.api.house.dto.internal.HouseDealView;
import com.ssafy.house.api.house.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI 도구 호출에서 사용하는 아파트 및 매물 조회 기능을 제공한다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HouseTools {
    private final HouseRepository houseRepository;

    private static final String SIDO_DESC = "시('Metropolitan City' or 'Si'), 도('Self-Governing Province' or 'Do') 이름 일부 ";
    private static final String GUGUN_DESC = "구('Gu')군('Gun') 이름 일부 ";
    private static final String DONG_DESC = "읍('Eup')면('Myeon')동('Dong') 이름 일부 ";
    private static final String APT_NM_DESC = "아파트 이름 일부 ";
    private static final String TRADE_TYPE_DESC = "거래유형 (매매/전세/월세) ";

    /**
     * 아파트명 일부로 아파트 식별자 목록을 조회한다.
     *
     * @param partialName 아파트명 일부
     * @return 아파트 식별자 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    @Tool(description = "입력한 아파트명 일부로 아파트 검색")
    public List<String> searchHouseByPartialName(@ToolParam(description = APT_NM_DESC) String partialName)
            throws SQLException {
        log.warn("입력한 아파트명 일부로 아파트 검색");
        try {
            return houseRepository.searchByAptName(partialName);
        } catch (Exception e) {
            log.error("검색 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 시도명 일부로 아파트 식별자 목록을 조회한다.
     *
     * @param sido 시도명 일부
     * @return 아파트 식별자 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    @Tool(description = SIDO_DESC +
            "로 아파트 검색")
    public List<String> searchBySido(
            @ToolParam(description = SIDO_DESC) String sido)
            throws SQLException {
        log.warn("시도로 아파트 검색");
        return houseRepository.findByOptionsAndAptName(sido, null, null, null);
    }

    /**
     * 시도와 구군 조건으로 아파트 식별자 목록을 조회한다.
     *
     * @param sido 시도명 일부
     * @param gugun 구군명 일부
     * @return 아파트 식별자 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    @Tool(description = SIDO_DESC + GUGUN_DESC +
            "로 아파트 검색")
    public List<String> searchByGugun(
            @ToolParam(description = SIDO_DESC) String sido,
            @ToolParam(description = GUGUN_DESC) String gugun) throws SQLException {
        log.warn("시도구군으로 아파트 검색");
        return houseRepository.findByOptionsAndAptName(sido, gugun, null, null);
    }

    /**
     * 시도, 구군, 동 조건으로 아파트 식별자 목록을 조회한다.
     *
     * @param dong 동명 일부
     * @param gugun 구군명 일부
     * @param sido 시도명 일부
     * @return 아파트 식별자 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    @Tool(description = SIDO_DESC + GUGUN_DESC + DONG_DESC +
            "로 아파트 검색")
    public List<String> searchByDong(
            @ToolParam(description = DONG_DESC) String dong,
            @ToolParam(description = GUGUN_DESC) String gugun,
            @ToolParam(description = SIDO_DESC) String sido) throws SQLException {
        log.warn("시도구군읍면동으로 아파트 검색");
        return houseRepository.findByOptionsAndAptName(sido, gugun, dong, null);
    }

    /**
     * 지역과 아파트명 조건으로 아파트 식별자 목록을 조회한다.
     *
     * @param sido 시도명 일부
     * @param gugun 구군명 일부
     * @param dong 동명 일부
     * @param aptNm 아파트명 일부
     * @return 아파트 식별자 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    @Tool(description = SIDO_DESC + GUGUN_DESC + DONG_DESC + APT_NM_DESC +
            "로 아파트 검색")
    public List<String> searchByOptionsAndName(
            @ToolParam(description = SIDO_DESC) String sido,
            @ToolParam(description = GUGUN_DESC) String gugun,
            @ToolParam(description = DONG_DESC) String dong,
            @ToolParam(description = APT_NM_DESC) String aptNm) throws SQLException {
        log.warn("시도구군읍면동아파트명으로 아파트 검색");
        return houseRepository.findByOptionsAndAptName(sido, gugun, dong, aptNm);
    }

    /**
     * 지역, 아파트명, 거래유형 조건으로 매물 목록을 조회한다.
     *
     * @param sido 시도명 일부
     * @param gugun 구군명 일부
     * @param dong 동명 일부
     * @param aptNm 아파트명 일부
     * @param tradeType 거래유형
     * @return 매물 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    @Tool(description = SIDO_DESC + GUGUN_DESC + DONG_DESC + APT_NM_DESC + TRADE_TYPE_DESC +
            "로 매물 조회")
    public List<HouseDealView> searchDealsByOptionsAndType(
            @ToolParam(description = SIDO_DESC + " (nullable)") String sido,
            @ToolParam(description = GUGUN_DESC + " (nullable)") String gugun,
            @ToolParam(description = DONG_DESC + " (nullable)") String dong,
            @ToolParam(description = APT_NM_DESC + " (nullable)") String aptNm,
            @ToolParam(description = TRADE_TYPE_DESC) String tradeType) throws SQLException {
        log.warn("시도구군읍면동아파트명거래유형으로 매물 조회");
        return houseRepository.findDealsByOptionsAndType(sido, gugun, dong, aptNm, tradeType);
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
    @Tool(description = SIDO_DESC + GUGUN_DESC + DONG_DESC + APT_NM_DESC
            + "예산 이하 매매/전세 매물 조회")
    public List<HouseDealView> searchDealsByBudget(
            @ToolParam(description = SIDO_DESC + " (nullable)") String sido,
            @ToolParam(description = GUGUN_DESC + " (nullable)") String gugun,
            @ToolParam(description = DONG_DESC + " (nullable)") String dong,
            @ToolParam(description = APT_NM_DESC + " (nullable)") String aptNm,
            @ToolParam(description = "최대 가격 (원)") long maxPrice)
            throws SQLException {
        return houseRepository.findDealsByBudget(sido, gugun, dong, aptNm, maxPrice);
    }

    /**
     * 보증금과 월세 한도 이하의 월세 매물을 조회한다.
     *
     * @param sido 시도명 일부
     * @param gugun 구군명 일부
     * @param dong 동명 일부
     * @param aptNm 아파트명 일부
     * @param maxDeposit 최대 보증금
     * @param maxRent 최대 월세
     * @return 월세 매물 목록
     * @throws SQLException 조회 중 예외가 발생한 경우
     */
    @Tool(description = SIDO_DESC + GUGUN_DESC + DONG_DESC + APT_NM_DESC
            + "보증금·월세 한도로 월세 매물 조회")
    public List<HouseDealView> searchRentDeals(
            @ToolParam(description = SIDO_DESC + " (nullable)") String sido,
            @ToolParam(description = GUGUN_DESC + " (nullable)") String gugun,
            @ToolParam(description = DONG_DESC + " (nullable)") String dong,
            @ToolParam(description = APT_NM_DESC + " (nullable)") String aptNm,
            @ToolParam(description = "최대 보증금 (원)") long maxDeposit,
            @ToolParam(description = "최대 월세 (원)") int maxRent)
            throws SQLException {
        return houseRepository.findRentDeals(sido, gugun, dong, aptNm, maxDeposit, maxRent);
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
    @Tool(description = SIDO_DESC + GUGUN_DESC + DONG_DESC + APT_NM_DESC
            + "옵션 지역별 최저가 매물 조회")
    public HouseDealView searchLowestDeal(
            @ToolParam(description = SIDO_DESC + " (nullable)") String sido,
            @ToolParam(description = GUGUN_DESC + " (nullable)") String gugun,
            @ToolParam(description = DONG_DESC + " (nullable)") String dong,
            @ToolParam(description = APT_NM_DESC + " (nullable)") String aptNm,
            @ToolParam(description = "거래유형 (매매/전세/월세)") String tradeType)
            throws SQLException {
        return houseRepository.findLowestDeal(
                sido, gugun, dong, aptNm, tradeType);
    }

    /**
     * 조건에 맞는 가격 범위의 매물을 조회한다.
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
    @Tool(description = SIDO_DESC + GUGUN_DESC + DONG_DESC + APT_NM_DESC
            + "옵션 지역별 가격 범위 매물 조회")
    public List<HouseDealView> searchDealsByPriceRange(
            @ToolParam(description = SIDO_DESC + " (nullable)") String sido,
            @ToolParam(description = GUGUN_DESC + " (nullable)") String gugun,
            @ToolParam(description = DONG_DESC + " (nullable)") String dong,
            @ToolParam(description = APT_NM_DESC + " (nullable)") String aptNm,
            @ToolParam(description = "최소 가격 (원)") long minPrice,
            @ToolParam(description = "최대 가격 (원)") long maxPrice)
            throws SQLException {
        return houseRepository.findDealsByPriceRange(
                sido, gugun, dong, aptNm, minPrice, maxPrice);
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
    @Tool(description = SIDO_DESC + GUGUN_DESC + DONG_DESC + APT_NM_DESC
            + "옵션 지역별 최고가 매물 조회")
    public HouseDealView searchHighestDeal(
            @ToolParam(description = SIDO_DESC + " (nullable)") String sido,
            @ToolParam(description = GUGUN_DESC + " (nullable)") String gugun,
            @ToolParam(description = DONG_DESC + " (nullable)") String dong,
            @ToolParam(description = APT_NM_DESC + " (nullable)") String aptNm,
            @ToolParam(description = "거래유형 (매매/전세/월세)") String tradeType)
            throws SQLException {
        return houseRepository.findHighestDeal(
                sido, gugun, dong, aptNm, tradeType);
    }
}

