package com.ssafy.house.ai.tools;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.ssafy.house.model.service.HouseInfoService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HouseTools {
    private final HouseInfoService houseInfoService;
    private Logger logger = LoggerFactory.getLogger(HouseTools.class);

    @Tool(description = "입력한 아파트명 일부로 아파트 검색")
    public List<String> searchHouseByPartialName(@ToolParam(description = "아파트 이름 일부") String partialName)
            throws SQLException {
        logger.warn("searchHouseByPartialName called with partialName={}", partialName);
        try {
            return houseInfoService.searchByAptName(partialName);
        } catch (Exception e) {
            logger.error("검색 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    @Tool(description = "시도(sido), 구군(gugun)으로 아파트 검색 (부분 매칭)")
    public List<String> searchByGugun(
            @ToolParam(description = "시도 이름") String sido,
            @ToolParam(description = "구군 이름") String gugun) {
        logger.warn("searchByGugun called with sido={} and gugun={}", sido, gugun);
        return houseInfoService.findBySidoGugun(sido, gugun);
    }

    @Tool(description = "시도(sido), 구군(gugun), 읍면동(dong)으로 아파트 검색 (부분 매칭)")
    public List<String> searchByDong(
            @ToolParam(description = "시도 이름") String sido,
            @ToolParam(description = "구군 이름") String gugun,
            @ToolParam(description = "읍면동 이름") String dong) {
        logger.warn("searchByDong called with sido={}, gugun={}, dong={}", sido, gugun, dong);
        return houseInfoService.findBySidoGugunDong(sido, gugun, dong);
    }

    @Tool(description = "시도, 구군, 읍면동 조합으로 아파트 검색 (부분 매칭)")
    public List<String> searchByFilters(
            @ToolParam(description = "시도 이름") String sido,
            @ToolParam(description = "구군 이름") String gugun,
            @ToolParam(description = "읍면동 이름") String dong) {
        logger.warn("searchByFilters called with sido={}, gugun={}, dong={}", sido, gugun, dong);
        return houseInfoService.findByOptional(sido, gugun, dong);
    }

    @Tool(description = "시도, 구군, 읍면동 + 아파트명 조합으로 검색 (부분 매칭)")
    public List<String> searchByFiltersAndName(
            @ToolParam(description = "시도 이름") String sido,
            @ToolParam(description = "구군 이름") String gugun,
            @ToolParam(description = "읍면동 이름") String dong,
            @ToolParam(description = "아파트 이름 일부") String aptNm) {
        logger.warn("searchByFiltersAndName called with sido={}, gugun={}, dong={}, aptNm={}",
                sido, gugun, dong, aptNm);
        return houseInfoService.findByFiltersAndName(sido, gugun, dong, aptNm);
    }
}
