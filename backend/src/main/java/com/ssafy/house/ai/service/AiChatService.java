package com.ssafy.house.ai.service;

import com.ssafy.house.model.dto.ChatResponseDto;
import com.ssafy.house.model.dto.CustomChatResponseDto;

public interface AiChatService {
    ChatResponseDto houseToolGeneration(String userInput);

    CustomChatResponseDto userControlledChat(String userInput, String convoId);
}
