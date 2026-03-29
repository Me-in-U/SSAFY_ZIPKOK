package com.ssafy.house.api.ai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.api.ai.dto.request.AiChatRequest;
import com.ssafy.house.api.ai.dto.response.CustomChatResponseDto;
import com.ssafy.house.api.ai.service.AiChatService;
import com.ssafy.house.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * AI 채팅 엔드포인트를 제공하는 컨트롤러이다.
 */
@RestController
@RequestMapping({"/v1/ai", "/api/v1/ai", "/ai"})
@RequiredArgsConstructor
@Tag(name = "AI", description = "AI 채팅 관련 기능 제공")
public class AIChatController {
    private final AiChatService chatService;

    /**
     * 사용자 메시지를 AI 채팅 서비스에 전달하고 응답을 반환한다.
     *
     * @param body 채팅 요청 본문
     * @return AI 채팅 응답
     */
    @PostMapping("/user-controlled")
    @Operation(summary = "아파트 정보, 멤버, 시간 등을 관리하는 Chat API", description = "아파트 정보, 멤버, 시간 등을 관리하는 Chat API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<CustomChatResponseDto> userControlledChat(@RequestBody AiChatRequest body) {
        CustomChatResponseDto dto = chatService.userControlledChat(body.message(), body.convoId());
        return BaseResponse.onSuccess(dto);
    }
}
