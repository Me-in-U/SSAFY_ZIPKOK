package com.ssafy.house.api.ai.dto.request;

/**
 * AI 채팅 요청 본문을 표현하는 DTO이다.
 *
 * @param message 사용자가 입력한 메시지
 * @param convoId 기존 대화가 있으면 이어서 사용할 대화 식별자
 */
public record AiChatRequest(String message, String convoId) {
}
