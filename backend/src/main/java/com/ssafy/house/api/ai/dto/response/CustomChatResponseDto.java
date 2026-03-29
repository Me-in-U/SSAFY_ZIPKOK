package com.ssafy.house.api.ai.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 채팅 응답 본문을 표현하는 DTO이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomChatResponseDto {
    private String message;
    private List<String> aptSeqList;
    private List<String> relatedQuestionList;
    private String convoId;
}
