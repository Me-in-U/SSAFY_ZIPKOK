package com.ssafy.house.ai.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.ai.service.AiChatService;
import com.ssafy.house.restcontroller.RestControllerHelper;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "https://ssafy.blog",
        "http://192.168.204.108:5173/",
        "http://172.22.16.1:5173/" })
public class AIChatController implements RestControllerHelper {
    private final AiChatService chatService;

    @PostMapping("/advised")
    @Operation(summary = "조언 생성 Chat API", description = "조언을 생성하는 Chat API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조언 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<?> advisedGeneration(@RequestBody Map<String, String> userInput) {
        Object result = chatService.advisedGeneration(userInput.get("message"));
        return handleSuccess(Map.of("message", result));
    }

    @PostMapping("/tool")
    @Operation(summary = "잡다한 기능에 대한 AI TOOL Chat API", description = "잡다한 기능 Chat API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "도구 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<?> toolGeneration(@RequestBody Map<String, String> userInput) {
        Object result = chatService.timeToolGeneration(userInput.get("message"));
        return handleSuccess(Map.of("message", result));
    }

    @PostMapping("/member")
    @Operation(summary = "회원 정보를 관리하는 AI TOOL Chat API", description = "회원 정보를 관리하는 Chat API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 도구 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<?> toolMemberGeneration(@RequestBody Map<String, String> userInput) {
        Object result = chatService.memberToolGeneration(userInput.get("message"));
        return handleSuccess(Map.of("message", result));
    }
}
