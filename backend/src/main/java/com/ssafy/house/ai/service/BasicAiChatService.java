package com.ssafy.house.ai.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.house.ai.tools.DateTimeTools;
import com.ssafy.house.ai.tools.HouseTools;
import com.ssafy.house.ai.tools.MemberTools;
import com.ssafy.house.model.dto.HouseInfo;
import com.ssafy.house.model.service.HouseInfoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import com.fasterxml.jackson.core.type.TypeReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAiChatService implements AiChatService {
    @Qualifier("advisedChatClient")
    private final ChatClient advisedChatClient;
    private final DateTimeTools dateTimeTools;
    private final MemberTools memberTools;
    private final HouseTools houseTools;
    private final HouseInfoService houseInfoService;

    @Override
    public String timeToolGeneration(String userInput) {
        return this.advisedChatClient
                .prompt()
                .system(c -> c.param("language", "Korean")
                        .param("character", "Chill한"))
                .user(userInput)
                .tools(dateTimeTools)
                .call()
                .content();
    }

    @Override
    public String memberToolGeneration(String userInput) {
        return this.advisedChatClient
                .prompt()
                .system(c -> c.param("language", "Korean")
                        .param("character", "Chill한"))
                .user(userInput)
                .tools(memberTools)
                .call()
                .content();
    }

    @Override
    public Map<String, Object> houseToolGeneration(String userInput) {
        // 1. 시스템·캐릭터 설정 추가
        ChatResponse response = advisedChatClient
                .prompt()
                .system(c -> c.param("language", "Korean").param("character", "Chill한"))
                .user(userInput)
                .tools(houseTools)
                .call()
                .chatResponse();

        // 2. 메시지·툴 호출 결과 처리
        String message = response.getResult().getOutput().getText();

        // 4. Tool 호출 결과에서 arguments 추출
        String aptName = null;
        if (response.getResult().getOutput().hasToolCalls()) {
            var toolCalls = response.getResult().getOutput().getToolCalls();
            if (!toolCalls.isEmpty()) {
                String argsJson = toolCalls.get(0).arguments();
                try {
                    var argsMap = new ObjectMapper()
                            .readValue(argsJson, new TypeReference<Map<String, Object>>() {
                            });
                    aptName = (String) argsMap.get("partialName");
                } catch (Exception e) {
                    log.error("Tool arguments parsing failed", e);
                }
            }
        }

        // 5. 아파트 정보 검색
        List<HouseInfo> houses = null;
        try {
            houses = (aptName != null)
                    ? houseInfoService.searchByAptName(aptName)
                    : List.of();
        } catch (SQLException e) {
            log.error("Database error occurred", e);
        }

        // 6. 응답 반환
        return Map.of("message", message, "houses", houses);

    }

}
