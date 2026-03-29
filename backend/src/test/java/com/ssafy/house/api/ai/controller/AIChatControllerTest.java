package com.ssafy.house.api.ai.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.ssafy.house.api.ai.dto.response.CustomChatResponseDto;
import com.ssafy.house.api.ai.service.AiChatService;
import com.ssafy.house.global.security.config.SecurityConfig;
import com.ssafy.house.global.security.jwt.JwtUtil;

@WebMvcTest(controllers = AIChatController.class, excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class))
@AutoConfigureMockMvc(addFilters = false)
@Import(AIChatControllerTest.MockConfig.class)
@ImportAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
class AIChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AiChatService aiChatService;

    @Test
    void userControlledChat_returnsBaseResponseOnV1Path() throws Exception {
        when(aiChatService.userControlledChat("반포자이 찾아줘", "convo-1"))
                .thenReturn(CustomChatResponseDto.builder()
                        .message("반포자이 아파트를 찾았어요.")
                        .aptSeqList(java.util.List.of("1001"))
                        .relatedQuestionList(java.util.List.of("반포자이 아파트 매매 가격 알려줘"))
                        .convoId("convo-1")
                        .build());

        mockMvc.perform(post("/v1/ai/user-controlled")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "message": "반포자이 찾아줘",
                          "convoId": "convo-1"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.message").value("반포자이 아파트를 찾았어요."))
                .andExpect(jsonPath("$.result.aptSeqList[0]").value("1001"))
                .andExpect(jsonPath("$.result.convoId").value("convo-1"));
    }

    @Test
    void userControlledChat_keepsLegacyCompatibilityPath() throws Exception {
        when(aiChatService.userControlledChat("잠실 찾아줘", null))
                .thenReturn(CustomChatResponseDto.builder()
                        .message("잠실 아파트를 찾았어요.")
                        .aptSeqList(java.util.List.of("2001"))
                        .relatedQuestionList(java.util.List.of())
                        .convoId("convo-2")
                        .build());

        mockMvc.perform(post("/api/v1/ai/user-controlled")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "message": "잠실 찾아줘",
                          "convoId": null
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.message").value("잠실 아파트를 찾았어요."))
                .andExpect(jsonPath("$.result.aptSeqList[0]").value("2001"))
                .andExpect(jsonPath("$.result.convoId").value("convo-2"));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        AiChatService aiChatService() {
            return Mockito.mock(AiChatService.class);
        }

        @Bean
        JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }

        @Bean
        UserDetailsService userDetailsService() {
            return Mockito.mock(UserDetailsService.class);
        }

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }
}
