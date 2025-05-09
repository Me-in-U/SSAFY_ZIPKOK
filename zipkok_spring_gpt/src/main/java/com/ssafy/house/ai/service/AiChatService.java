package com.ssafy.house.ai.service;

import java.util.function.Consumer;

import org.springframework.ai.chat.client.ChatClient.AdvisorSpec;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;

public interface AiChatService {
    public record Quiz(String question, String answer, String reason) {
    }

    public default Object simpleGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    };

    public default Quiz quiz(String category) {
        throw new RuntimeException("not yet ready");
    };

    public default String multiModal(String userInput, MimeType mime, Resource resource) {
        throw new RuntimeException("not yet ready");
    }

    public default String reReadingGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    };

    public default String advisedGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    }

    public default String timeToolGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    }

    public default String toolFunctionBaseGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    }

    public default String memberToolGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    }

    public default String ragGeneration(String userInput, boolean isStrict, Consumer<AdvisorSpec> advisorSpec) {
        throw new RuntimeException("not yet ready");
    }

}
