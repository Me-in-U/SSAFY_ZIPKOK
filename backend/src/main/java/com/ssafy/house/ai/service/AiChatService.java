package com.ssafy.house.ai.service;

import com.ssafy.house.model.dto.CustomChatResponseDto;

public interface AiChatService {
    CustomChatResponseDto userControlledChat(String userInput, String convoId);
}
