package com.ssafy.house.ai.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

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
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
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
    private static final String MODEL_ERROR_MESSAGE = "모델 호출 중 오류가 발생했습니다.";
    private static final String FALLBACK_MESSAGE = "응답을 구조화하지 못해 도구 결과만 반환합니다.";
    private static final List<String> SIMPLE_SEARCH_SUFFIXES = List.of("찾아줘", "찾아 줘", "찾아봐", "찾아 봐");
    private static final List<String> APARTMENT_NAME_HINTS = List.of(
            "아파트", "자이", "래미안", "푸르지오", "아이파크", "더샵", "롯데캐슬", "힐스테이트",
            "e편한세상", "이편한세상", "주공", "캐슬", "하이츠", "팰리스", "타워", "맨션",
            "단지", "1차", "2차", "3차", "우성", "현대", "대림", "한신", "삼성");
    private static final List<String> SIMPLE_SEARCH_EXCLUSIONS = List.of(
            "맛집", "식당", "카페", "영화", "노래", "사람", "책", "코드", "주식", "날씨", "병원");
    private static final int MAX_SIMPLE_SEARCH_KEYWORD_LENGTH = 20;

    @Value("${ssafy.ai.custom-system-prompt}")
    private String customSystemPrompt;

    private final DateTimeTools dateTimeTools;
    // private final MemberTools memberTools; // 미사용
    private final HouseTools houseTools;

    private final ChatModel chatModel;
    private final ToolCallingManager toolCallingManager;
    private final ChatMemory chatMemory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 아파트 정보 변환
    private final BeanOutputConverter<CustomChatResponseDto> customDtoConverter = new BeanOutputConverter<>(
            CustomChatResponseDto.class);

    @Override
    public CustomChatResponseDto userControlledChat(String userInput, String convoId) {
        convoId = initializeConversation(convoId);

        CustomChatResponseDto shortcutResponse = tryHandleSimpleApartmentSearch(userInput, convoId);
        if (shortcutResponse != null) {
            return shortcutResponse;
        }

        ChatOptions opts = createChatOptions();
        List<String> toolPayloads = new ArrayList<>();
        String normalizedUserInput = normalizeUserInput(userInput);
        chatMemory.add(convoId, new UserMessage(normalizedUserInput));

        Prompt prompt = new Prompt(chatMemory.get(convoId), opts);
        ChatResponse chatResponse;
        try {
            chatResponse = chatModel.call(prompt);
        } catch (Exception e) {
            log.error("초기 모델 호출 중 오류 발생", e);
            return errorResponse(convoId, MODEL_ERROR_MESSAGE);
        }
        chatMemory.add(convoId, chatResponse.getResult().getOutput());

        while (chatResponse.hasToolCalls()) {
            ToolExecutionResult exec = toolCallingManager.executeToolCalls(prompt, chatResponse);
            ToolResponseMessage toolResponseMessage = exec.conversationHistory().stream()
                    .filter(m -> m instanceof ToolResponseMessage)
                    .map(m -> (ToolResponseMessage) m)
                    .reduce((first, second) -> second)
                    .orElseThrow(() -> new IllegalStateException("툴 응답이 없습니다"));

            toolResponseMessage.getResponses().stream()
                    .map(ToolResponseMessage.ToolResponse::responseData)
                    .filter(Objects::nonNull)
                    .forEach(toolPayloads::add);
            log.warn("[툴 호출 결과 원본]: {}", toolPayloads);
            chatMemory.add(convoId, toolResponseMessage);

            prompt = new Prompt(exec.conversationHistory(), opts);
            try {
                chatResponse = chatModel.call(prompt);
                chatMemory.add(convoId, chatResponse.getResult().getOutput());
                log.warn("[툴 호출 결과로 GPT 반응 원본]: {}", chatResponse.getResult().getOutput());
            } catch (Exception e) {
                log.error("툴 호출 후 모델 재호출 중 오류 발생", e);
                return fallbackResponse(convoId, MODEL_ERROR_MESSAGE, toolPayloads);
            }
        }

        String lastResponse = chatMemory.get(convoId).stream()
                .filter(m -> m instanceof AssistantMessage)
                .map(m -> ((AssistantMessage) m).getText())
                .reduce((first, second) -> second)
                .orElse("");
        log.warn("[최종 AssistantMessage]: {}", lastResponse);

        String jsonResponse = extractJsonObject(lastResponse);
        if (jsonResponse == null) {
            return fallbackResponse(convoId, FALLBACK_MESSAGE, toolPayloads);
        }

        try {
            CustomChatResponseDto dto = customDtoConverter.convert(jsonResponse);
            dto.setConvoId(convoId);
            return dto;
        } catch (Exception e) {
            log.error("CustomChatResponseDto 변환 실패", e);
            return fallbackResponse(convoId, FALLBACK_MESSAGE, toolPayloads);
        }
    }

    private String initializeConversation(String convoId) {
        if (convoId != null && !convoId.isBlank()) {
            return convoId;
        }

        String newConvoId = UUID.randomUUID().toString();
        chatMemory.add(newConvoId, SystemMessage.builder()
                .text(customSystemPrompt)
                .metadata(Map.of("language", "Korean", "character", "Chill한"))
                .build());
        return newConvoId;
    }

    private CustomChatResponseDto tryHandleSimpleApartmentSearch(String userInput, String convoId) {
        String keyword = extractSimpleSearchKeyword(userInput);
        if (keyword == null) {
            return null;
        }

        try {
            List<String> aptSeqList = houseTools.searchHouseByPartialName(keyword);
            if (aptSeqList == null || aptSeqList.isEmpty()) {
                return null;
            }

            String apartmentKeyword = buildApartmentKeyword(keyword);
            CustomChatResponseDto dto = CustomChatResponseDto.builder()
                    .message(buildSimpleSearchMessage(apartmentKeyword, aptSeqList.size()))
                    .aptSeqList(aptSeqList)
                    .relatedQuestionList(List.of(
                            apartmentKeyword + " 매매 가격 알려줘",
                            apartmentKeyword + " 전세 매물 찾아줘",
                            apartmentKeyword + " 주변 학교 알려줘"))
                    .build();
            dto.setConvoId(convoId);

            chatMemory.add(convoId, new UserMessage(userInput));
            chatMemory.add(convoId, new AssistantMessage(toAssistantMessagePayload(dto)));
            return dto;
        } catch (Exception e) {
            log.debug("단순 아파트 검색 단축 경로 실패: {}", userInput, e);
            return null;
        }
    }

    private String normalizeUserInput(String userInput) {
        String keyword = extractSimpleSearchKeyword(userInput);
        if (keyword == null || !containsApartmentNameHint(keyword)) {
            return userInput;
        }
        return buildApartmentKeyword(keyword) + " 찾아줘";
    }

    private String extractSimpleSearchKeyword(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return null;
        }

        String trimmed = userInput.trim();
        for (String suffix : SIMPLE_SEARCH_SUFFIXES) {
            if (!trimmed.endsWith(suffix)) {
                continue;
            }

            String keyword = trimmed.substring(0, trimmed.length() - suffix.length()).trim();
            if (keyword.isBlank()
                    || keyword.length() > MAX_SIMPLE_SEARCH_KEYWORD_LENGTH
                    || keyword.split("\\s+").length > 2
                    || containsSimpleSearchExclusion(keyword)) {
                return null;
            }
            return keyword;
        }
        return null;
    }

    private boolean containsSimpleSearchExclusion(String keyword) {
        return SIMPLE_SEARCH_EXCLUSIONS.stream().anyMatch(keyword::contains);
    }

    private boolean containsApartmentNameHint(String keyword) {
        return APARTMENT_NAME_HINTS.stream().anyMatch(keyword::contains);
    }

    private String buildApartmentKeyword(String keyword) {
        if (keyword.contains("아파트")) {
            return keyword;
        }
        return keyword + " 아파트";
    }

    private String buildSimpleSearchMessage(String apartmentKeyword, int resultCount) {
        if (resultCount == 1) {
            return apartmentKeyword + "를 찾았어요.";
        }
        return apartmentKeyword + "를 " + resultCount + "개 찾았어요.";
    }

    private String toAssistantMessagePayload(CustomChatResponseDto dto) throws IOException {
        return objectMapper.writeValueAsString(dto);
    }

    private ChatOptions createChatOptions() {
        return OpenAiChatOptions.builder()
                .toolCallbacks(ToolCallbacks.from(houseTools, dateTimeTools))
                .internalToolExecutionEnabled(false)
                .parallelToolCalls(false)
                .responseFormat(ResponseFormat.builder()
                        .type(ResponseFormat.Type.JSON_SCHEMA)
                        .jsonSchema(ResponseFormat.JsonSchema.builder()
                                .name("custom_chat_response")
                                .schema(customDtoConverter.getJsonSchemaMap())
                                .strict(Boolean.TRUE)
                                .build())
                        .build())
                .build();
    }

    private CustomChatResponseDto fallbackResponse(String convoId, String defaultMessage, List<String> toolPayloads) {
        List<String> aptSeqList = extractAptSeqList(toolPayloads);
        String message = aptSeqList.isEmpty()
                ? defaultMessage
                : "결과를 구조화하지 못해 도구 결과만 반환합니다. " + aptSeqList.size() + "개 찾았어요.";

        CustomChatResponseDto dto = CustomChatResponseDto.builder()
                .message(message)
                .aptSeqList(aptSeqList)
                .build();
        dto.setConvoId(convoId);
        return dto;
    }

    private CustomChatResponseDto errorResponse(String convoId, String message) {
        CustomChatResponseDto dto = CustomChatResponseDto.builder()
                .message(message)
                .aptSeqList(List.of())
                .build();
        dto.setConvoId(convoId);
        return dto;
    }

    private List<String> extractAptSeqList(List<String> toolPayloads) {
        Set<String> aptSeqSet = new LinkedHashSet<>();

        for (String toolPayload : toolPayloads) {
            try {
                JsonNode jsonNode = objectMapper.readTree(toolPayload);
                collectAptSeq(jsonNode, aptSeqSet);
            } catch (IOException e) {
                try {
                    aptSeqSet.addAll(objectMapper.readValue(toolPayload, new TypeReference<List<String>>() {
                    }));
                } catch (IOException ex) {
                    log.debug("도구 결과에서 aptSeqList 추출 실패: {}", toolPayload, ex);
                }
            }
        }

        return List.copyOf(aptSeqSet);
    }

    private void collectAptSeq(JsonNode jsonNode, Set<String> aptSeqSet) {
        if (jsonNode == null || jsonNode.isNull()) {
            return;
        }

        if (jsonNode.isArray()) {
            boolean allTextual = true;
            for (JsonNode child : jsonNode) {
                if (!child.isTextual()) {
                    allTextual = false;
                    break;
                }
            }

            if (allTextual) {
                for (JsonNode child : jsonNode) {
                    aptSeqSet.add(child.asText());
                }
                return;
            }

            for (JsonNode child : jsonNode) {
                collectAptSeq(child, aptSeqSet);
            }
            return;
        }

        if (jsonNode.isObject()) {
            addIfTextual(jsonNode.get("aptSeq"), aptSeqSet);
            addIfTextual(jsonNode.get("apt_seq"), aptSeqSet);
            addIfTextual(jsonNode.get("aptSeqList"), aptSeqSet);
        }
    }

    private void addIfTextual(JsonNode jsonNode, Set<String> aptSeqSet) {
        if (jsonNode == null || jsonNode.isNull()) {
            return;
        }

        if (jsonNode.isTextual()) {
            aptSeqSet.add(jsonNode.asText());
            return;
        }

        if (jsonNode.isArray()) {
            for (JsonNode child : jsonNode) {
                if (child.isTextual()) {
                    aptSeqSet.add(child.asText());
                }
            }
        }
    }

    private String extractJsonObject(String response) {
        if (response == null || response.isBlank()) {
            return null;
        }

        String trimmed = response.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return trimmed;
        }

        int start = -1;
        int depth = 0;
        boolean inString = false;
        boolean escaping = false;

        for (int i = 0; i < response.length(); i++) {
            char current = response.charAt(i);

            if (escaping) {
                escaping = false;
                continue;
            }

            if (current == '\\' && inString) {
                escaping = true;
                continue;
            }

            if (current == '"') {
                inString = !inString;
                continue;
            }

            if (inString) {
                continue;
            }

            if (current == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (current == '}' && depth > 0) {
                depth--;
                if (depth == 0 && start >= 0) {
                    return response.substring(start, i + 1).trim();
                }
            }
        }

        return null;
    }

}
