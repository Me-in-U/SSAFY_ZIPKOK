package com.ssafy.house.ai.tools;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.ssafy.house.model.dto.HouseDeal;
import com.ssafy.house.model.service.HouseDealService;
import com.ssafy.house.model.service.HouseInfoService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HouseTools {
    private final HouseInfoService houseInfoService;
    private final HouseDealService houseDealService;
    private Logger logger = LoggerFactory.getLogger(HouseTools.class);

    private static final String SIDO_DESC = "시('Metropolitan City' or 'Si'), 도('Self-Governing Province' or 'Do') 이름 일부 ";
    private static final String GUGUN_DESC = "구('Gu')군('Gun') 이름 일부 ";
    private static final String DONG_DESC = "읍('Eup')면('Myeon')동('Dong') 이름 일부 ";
    private static final String APT_NM_DESC = "아파트 이름 일부 ";
    private static final String TRADE_TYPE_DESC = "거래유형 (매매/전세/월세) ";

    @Tool(description = "입력한 아파트명 일부로 아파트 검색")
    public List<String> searchHouseByPartialName(@ToolParam(description = APT_NM_DESC) String partialName)
            throws SQLException {
        logger.warn("입력한 아파트명 일부로 아파트 검색");
        try {
            return houseInfoService.searchByAptName(partialName);
        } catch (Exception e) {
            logger.error("검색 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    @Tool(description = SIDO_DESC + "로 아파트 검색")
    public List<String> searchBySido(
            @ToolParam(description = SIDO_DESC) String sido)
            throws SQLException {
        logger.warn("시도로 아파트 검색");
        return houseInfoService.findByOptionsAndAptName(sido, null, null, null);
    }

    @Tool(description = SIDO_DESC + GUGUN_DESC + "로 아파트 검색")
    public List<String> searchByGugun(
            @ToolParam(description = SIDO_DESC) String sido,
            @ToolParam(description = GUGUN_DESC) String gugun) throws SQLException {
        logger.warn("시도구군으로 아파트 검색");
        return houseInfoService.findByOptionsAndAptName(sido, gugun, null, null);
    }

    @Tool(description = SIDO_DESC + GUGUN_DESC + DONG_DESC + "로 아파트 검색")
    public List<String> searchByDong(
            @ToolParam(description = DONG_DESC) String dong,
            @ToolParam(description = GUGUN_DESC) String gugun,
            @ToolParam(description = SIDO_DESC) String sido) throws SQLException {
        logger.warn("시도구군읍면동으로 아파트 검색");
        return houseInfoService.findByOptionsAndAptName(sido, gugun, dong, null);
    }

    @Tool(description = SIDO_DESC + GUGUN_DESC + DONG_DESC + APT_NM_DESC + "로 아파트 검색")
    public List<String> searchByOptionsAndName(
            @ToolParam(description = SIDO_DESC) String sido,
            @ToolParam(description = GUGUN_DESC) String gugun,
            @ToolParam(description = DONG_DESC) String dong,
            @ToolParam(description = APT_NM_DESC) String aptNm) throws SQLException {
        logger.warn("시도구군읍면동아파트명으로 아파트 검색");
        return houseInfoService.findByOptionsAndAptName(sido, gugun, dong, aptNm);
    }

    @Tool(description = SIDO_DESC + GUGUN_DESC + DONG_DESC + APT_NM_DESC + TRADE_TYPE_DESC + "로 매물 조회")
    public List<HouseDeal> searchDealsByOptionsAndType(
            @ToolParam(description = SIDO_DESC) String sido,
            @ToolParam(description = GUGUN_DESC) String gugun,
            @ToolParam(description = DONG_DESC) String dong,
            @ToolParam(description = APT_NM_DESC) String aptNm,
            @ToolParam(description = TRADE_TYPE_DESC) String tradeType) throws SQLException {
        logger.warn("시도구군읍면동아파트명거래유형으로 매물 조회");
        return houseDealService.findDealsByOptionsAndType(sido, gugun, dong, aptNm, tradeType);
    }
}
