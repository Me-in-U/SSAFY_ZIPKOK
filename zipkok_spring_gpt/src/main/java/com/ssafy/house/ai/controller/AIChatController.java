package com.ssafy.house.ai.controller;

import java.util.Map;
import java.util.function.Consumer;

import org.springframework.ai.chat.client.ChatClient.AdvisorSpec;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.ai.service.AiChatService;
import com.ssafy.house.restcontroller.RestControllerHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class AIChatController implements RestControllerHelper {

    private final AiChatService chatService;

    @PostMapping("/simple")
    ResponseEntity<?> simpleGeneration(@RequestBody Map<String, String> userInput) {
        Object result = chatService.simpleGeneration(userInput.get("message"));
        return handleSuccess(Map.of("message", result));
    }

    @PostMapping("/advised")
    ResponseEntity<?> advisedGeneration(@RequestBody Map<String, String> userInput) {
        Object result = chatService.advisedGeneration(userInput.get("message"));
        return handleSuccess(Map.of("message", result));
    }

    @PostMapping("/tool")
    ResponseEntity<?> toolGeneration(@RequestBody Map<String, String> userInput) {
        Object result = chatService.timeToolGeneration(userInput.get("message"));
        return handleSuccess(Map.of("message", result));
    }

    @PostMapping("/vector")
    ResponseEntity<?> ragGeneration(@RequestBody Map<String, String> userInput) {
        Object result = chatService.ragGeneration(userInput.get("message"), true, null);
        return handleSuccess(Map.of("message", result));
    }

    @PostMapping("/member")
    ResponseEntity<?> toolMemberGeneration(@RequestBody Map<String, String> userInput) {
        Object result = chatService.memberToolGeneration(userInput.get("message"));
        return handleSuccess(Map.of("message", result));
    }
}
