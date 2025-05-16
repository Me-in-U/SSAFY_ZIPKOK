package com.ssafy.house.ai.service;

import com.ssafy.house.model.dto.ChatResponseDto;

public interface AiChatService {
    default String chatToolGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    }

    ChatResponseDto houseToolGeneration(String userInput);
}
