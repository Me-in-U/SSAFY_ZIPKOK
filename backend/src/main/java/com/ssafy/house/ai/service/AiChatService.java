package com.ssafy.house.ai.service;

import java.util.Map;

public interface AiChatService {
    public default String advisedGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    }

    public default String timeToolGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    }

    public default String memberToolGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    }

    Map<String, Object> houseToolGeneration(String userInput);

    // public default String ragGeneration(String userInput, boolean isStrict,
    // Consumer<AdvisorSpec> advisorSpec) {
    // throw new RuntimeException("not yet ready");
    // }
}
