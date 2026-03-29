package com.ssafy.house.api.ai.service;

import com.ssafy.house.api.ai.dto.response.CustomChatResponseDto;

/**
 * AI 채팅 애플리케이션 서비스의 계약을 정의한다.
 */
public interface AiChatService {

    /**
     * 사용자 입력과 대화 식별자를 기반으로 AI 채팅 응답을 생성한다.
     *
     * @param userInput 사용자 입력
     * @param convoId 대화 식별자
     * @return AI 채팅 응답
     */
    CustomChatResponseDto userControlledChat(String userInput, String convoId);
}
