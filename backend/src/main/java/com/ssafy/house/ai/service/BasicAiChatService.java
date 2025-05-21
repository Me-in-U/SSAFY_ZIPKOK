package com.ssafy.house.ai.service;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ssafy.house.ai.tools.DateTimeTools;
import com.ssafy.house.ai.tools.HouseTools;
import com.ssafy.house.ai.tools.MemberTools;
import com.ssafy.house.model.dto.ChatResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAiChatService implements AiChatService {
    @Qualifier("advisedChatClient")
    private final ChatClient advisedChatClient;
    private final DateTimeTools dateTimeTools;
    private final MemberTools memberTools;
    private final HouseTools houseTools;

    // 아파트 정보 변환
    private final BeanOutputConverter<ChatResponseDto> dtoConverter = new BeanOutputConverter<>(
            ChatResponseDto.class);

    @SuppressWarnings("null")
    @Override
    public ChatResponseDto houseToolGeneration(String userInput) {
        // 1) AI에 JSON 스키마(format) 붙여서 요청
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

        // (2) JSON → ChatResponseDto (aptSeqList만 채워진 상태) 변환
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
}
