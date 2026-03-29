package com.ssafy.house.api.ai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 채팅 관련 공통 빈을 등록하는 설정 클래스이다.
 */
@Configuration
public class AiConfig {

    /**
     * 대화 메모리 저장소를 생성한다.
     *
     * @return 채팅 메모리 구현체
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(100)
                .build();
    }

    /**
     * Spring AI 도구 호출 매니저를 생성한다.
     *
     * @return 도구 호출 매니저
     */
    @Bean
    public ToolCallingManager toolCallingManager() {
        return DefaultToolCallingManager.builder().build();
    }
}
