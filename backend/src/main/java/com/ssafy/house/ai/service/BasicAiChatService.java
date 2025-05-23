package com.ssafy.house.ai.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.house.ai.tools.DateTimeTools;
import com.ssafy.house.ai.tools.HouseTools;
import com.ssafy.house.ai.tools.MemberTools;
import com.ssafy.house.model.dto.ChatResponseDto;
import com.ssafy.house.model.dto.CustomChatResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAiChatService implements AiChatService {
    @Value("${ssafy.ai.custom-system-prompt}")
    String customSystemPrompt;

    @Qualifier("advisedChatClient")
    private final ChatClient advisedChatClient;
    private final DateTimeTools dateTimeTools;
    private final MemberTools memberTools;
    private final HouseTools houseTools;

    private final ChatModel chatModel;
    private final ToolCallingManager toolCallingManager;
    private final ChatMemory chatMemory;
    private String lastToolRaw = "";

    // 아파트 정보 변환
    private final BeanOutputConverter<ChatResponseDto> dtoConverter = new BeanOutputConverter<>(
            ChatResponseDto.class);
    private final BeanOutputConverter<CustomChatResponseDto> customDtoConverter = new BeanOutputConverter<>(
            CustomChatResponseDto.class);

    private Logger logger = org.slf4j.LoggerFactory.getLogger(BasicAiChatService.class);

    @SuppressWarnings("null")
    @Override
    public ChatResponseDto houseToolGeneration(String userInput) {
        String format = dtoConverter.getFormat();
        String json = advisedChatClient
                .prompt()
                .system(c -> c.param("language", "Korean").param("character", "Chill한"))
                .user(userInput + "\n\n" + format)
                .tools(houseTools)
                .tools(dateTimeTools)
                .tools(memberTools)
                .call()
                .content();

        ChatResponseDto dto;
        try {
            dto = dtoConverter.convert(json);
        } catch (Exception e) {
            log.error("ChatResponseDto 변환 실패", e);
            dto = ChatResponseDto.builder()
                    .message("아파트 정보 파싱에 실패했어요.")
                    .aptSeqList(List.of())
                    .build();
        }
        return dto;
    }

    @Override
    public CustomChatResponseDto userControlledChat(String userInput) {
        String returnFormat = customDtoConverter.getFormat();
        userInput = userInput + "\n\n" + returnFormat;

        // 옵션 준비 (memberTools: CustomerTools 대체)
        ChatOptions opts = ToolCallingChatOptions.builder()
                .toolCallbacks(ToolCallbacks.from(houseTools, dateTimeTools, memberTools))
                .internalToolExecutionEnabled(false)
                .build();

        // 대화 ID 생성 및 초기 메시지 메모리 저장
        String convoId = UUID.randomUUID().toString();
        chatMemory.add(convoId, SystemMessage.builder()
                .text(customSystemPrompt)
                .metadata(Map.of("language", "Korean", "character", "Chill한"))
                .build());
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
            chatMemory.add(convoId, new UserMessage(lastToolRaw));
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
                        .toolResultList(List.of())
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
            List<Object> fallbackList = new ArrayList<>();
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<String> list = mapper.readValue(
                        lastToolRaw,
                        new TypeReference<List<String>>() {
                        });
                fallbackList.addAll(list);
            } catch (IOException ex) {
                log.error("raw JSON 파싱 실패", ex);
            }
            dto = CustomChatResponseDto.builder()
                    .message("결과가 너무 많아요.. ㅠㅠㅠ" + fallbackList.size() + "개 찾았어요.")
                    .toolResultList(fallbackList)
                    .build();
        }
        return dto;
    }

}
