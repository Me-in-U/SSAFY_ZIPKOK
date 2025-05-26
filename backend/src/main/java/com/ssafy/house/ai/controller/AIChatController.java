package com.ssafy.house.ai.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.ai.service.AiChatService;
import com.ssafy.house.model.dto.CustomChatResponseDto;
import com.ssafy.house.restcontroller.RestControllerHelper;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "https://ssafy.blog",
        "https://api.ssafy.blog",
        "http://api.ssafy.blog",
        "http://192.168.204.108:5173/",
        "http://172.22.16.1:5173/" })
public class AIChatController implements RestControllerHelper {
    private final AiChatService chatService;

    @PostMapping("/user-controlled")
    @Operation(summary = "아파트 정보, 멤버, 시간 등을 관리하는 Chat API", description = "아파트 정보, 멤버, 시간 등을 관리하는 Chat API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> userControlledChat(@RequestBody Map<String, String> body) {
        CustomChatResponseDto dto = chatService.userControlledChat(body.get("message"), body.get("convoId"));
        return ResponseEntity.ok(dto);
    }
}
