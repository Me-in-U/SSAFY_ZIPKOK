package com.ssafy.house.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomChatResponseDto {
    private String message;
    private List<Object> toolResultList;
    private List<String> relatedQuestionList;
}