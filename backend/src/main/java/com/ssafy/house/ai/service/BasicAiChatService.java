package com.ssafy.house.ai.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.house.ai.tools.DateTimeTools;
import com.ssafy.house.ai.tools.HouseTools;
import com.ssafy.house.model.dto.CustomChatResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAiChatService implements AiChatService {
    @Value("${ssafy.ai.custom-system-prompt}")
    String customSystemPrompt;

    private final DateTimeTools dateTimeTools;
    // private final MemberTools memberTools; // 미사용
    private final HouseTools houseTools;

    private final ChatModel chatModel;
    private final ToolCallingManager toolCallingManager;
    private final ChatMemory chatMemory;
    private String lastToolRaw = "";

    // 아파트 정보 변환
    private final BeanOutputConverter<CustomChatResponseDto> customDtoConverter = new BeanOutputConverter<>(
            CustomChatResponseDto.class);

    private Logger logger = LoggerFactory.getLogger(BasicAiChatService.class);

    @Override
    public CustomChatResponseDto userControlledChat(String userInput, String convoId) {
        String returnFormat = customDtoConverter.getFormat();
        userInput = userInput + "\n\n" + returnFormat;

        // 옵션 준비 (memberTools: CustomerTools 대체)
        ChatOptions opts = ToolCallingChatOptions.builder()
                .toolCallbacks(ToolCallbacks.from(houseTools, dateTimeTools))
                .internalToolExecutionEnabled(false)
                .build();

        // convoId 가 비어 있으면 신규 생성, 아니라면 그대로 재사용
        if (convoId == null || convoId.isBlank()) {
            convoId = UUID.randomUUID().toString();
            // 최초 호출일 땐 시스템 메시지도 한 번만 저장
            chatMemory.add(convoId, SystemMessage.builder()
                    .text(customSystemPrompt)
                    .metadata(Map.of("language", "Korean", "character", "Chill한"))
                    .build());
        }
        chatMemory.add(convoId, new UserMessage(userInput));

        // 첫 프롬프트 생성 및 모델 호출
        Prompt prompt = new Prompt(chatMemory.get(convoId), opts);
        ChatResponse chatResponse = chatModel.call(prompt);
        chatMemory.add(convoId, chatResponse.getResult().getOutput());

        CustomChatResponseDto dto;
        // 툴 호출 루프
        while (chatResponse.hasToolCalls()) {
            ToolExecutionResult exec = toolCallingManager.executeToolCalls(prompt, chatResponse);

            // ───── 툴 결과 조회 ─────
            // conversationHistory() 맨 마지막에 ToolResponseMessage가 담겨 있으니 꺼냅니다.
            ToolResponseMessage toolResponseMessage = exec.conversationHistory().stream()
                    .filter(m -> m instanceof ToolResponseMessage)
                    .map(m -> (ToolResponseMessage) m)
                    .reduce((first, second) -> second) // 가장 마지막 ToolResponseMessage
                    .orElseThrow(() -> new IllegalStateException("툴 응답이 없습니다"));

            // ToolResponseMessage.getResponses() 에서 첫 번째 응답을 꺼내고 .responseData()로 원시 문자열 추출
            lastToolRaw = toolResponseMessage.getResponses().get(0).responseData();
            logger.warn("[툴 호출 결과 원본]: " + lastToolRaw);
            chatMemory.add(convoId, toolResponseMessage);
            // ─────────────────────────

            // 모델 재호출
            prompt = new Prompt(exec.conversationHistory(), opts);
            try {
                // 툴 호출 결과를 포함한 프롬프트로 모델 호출
                chatResponse = chatModel.call(prompt);
                chatMemory.add(convoId, chatResponse.getResult().getOutput());
                logger.warn("[툴 호출 결과로 GPT 반응 원본]: " + chatResponse.getResult().getOutput());
            } catch (Exception e) {
                logger.error("모델 호출 중 오류 발생: {}", e.getMessage());
                // 오류 발생 시 툴 호출 결과를 포함한 프롬프트로 모델 호출
                prompt = new Prompt(chatMemory.get(convoId), opts);
                dto = CustomChatResponseDto.builder()
                        .message("모델 호출 중 오류 발생")
                        .aptSeqList(List.of())
                        .build();
            }
        }
        // 마지막 AssistantMessage 텍스트 추출해 반환
        String lastResponse = chatMemory.get(convoId).stream()
                .filter(m -> m instanceof AssistantMessage)
                .map(m -> ((AssistantMessage) m).getText())
                .reduce((first, second) -> second)
                .orElse("");
        logger.warn("[최종 AssistantMessage]: " + lastResponse);
        try {
            if (lastResponse.startsWith("{") && lastResponse.endsWith("}")) {
                dto = customDtoConverter.convert(lastResponse);
            } else {
                throw new Exception("JSON 형식이 아닙니다.");
            }
        } catch (Exception e) {
            log.error("CustomChatResponseDto 변환 실패", e);
            List<String> fallbackList = new ArrayList<>();
            try {
                ObjectMapper mapper = new ObjectMapper();
                fallbackList = mapper.readValue(
                        lastToolRaw,
                        new TypeReference<List<String>>() {
                        });
            } catch (IOException ex) {
                log.error("raw JSON 파싱 실패", ex);
            }
            dto = CustomChatResponseDto.builder()
                    .message("결과가 너무 많아요.. ㅠㅠㅠ" + fallbackList.size() + "개 찾았어요.")
                    .aptSeqList(fallbackList)
                    .build();
        }
        dto.setConvoId(convoId);
        return dto;
    }

}
