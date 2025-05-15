package com.ssafy.house.ai.tools;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.ssafy.house.model.dto.HouseInfo;
import com.ssafy.house.model.service.HouseInfoService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HouseTools {
    private final HouseInfoService houseInfoService;
    private Logger logger = LoggerFactory.getLogger(HouseTools.class);

    @Tool(description = "입력한 아파트명 일부로 아파트 검색")
    public List<HouseInfo> searchHouseByPartialName(@ToolParam(description = "아파트 이름 일부") String partialName)
            throws SQLException {
        logger.info("searchHouseByPartialName called with partialName={}", partialName);
        try {
            return houseInfoService.searchByAptName(partialName);
        } catch (Exception e) {
            logger.error("검색 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

}
