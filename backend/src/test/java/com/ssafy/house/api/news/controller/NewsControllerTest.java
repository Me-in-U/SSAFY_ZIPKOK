package com.ssafy.house.api.news.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.ssafy.house.api.news.dto.response.NewsLatestResponse;
import com.ssafy.house.api.news.dto.response.NewsSummaryResponse;
import com.ssafy.house.api.news.service.NewsQueryService;
import com.ssafy.house.global.security.config.SecurityConfig;
import com.ssafy.house.global.security.jwt.JwtUtil;

@WebMvcTest(controllers = NewsController.class, excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class))
@AutoConfigureMockMvc(addFilters = false)
@Import(NewsControllerTest.MockConfig.class)
@ImportAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsQueryService newsQueryService;

    @Test
    void getLatestNews_returnsBaseResponse() throws Exception {
        NewsLatestResponse response = new NewsLatestResponse(List.of(
                new NewsSummaryResponse(
                        1,
                        "뉴스 제목",
                        "https://example.com/original",
                        "https://example.com/naver",
                        "설명",
                        LocalDateTime.of(2026, 3, 29, 14, 0))));
        when(newsQueryService.getLatestNews(5, 2)).thenReturn(response);

        mockMvc.perform(get("/v1/news/latest")
                .param("limit", "5")
                .param("offset", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andExpect(jsonPath("$.result.news[0].id").value(1))
                .andExpect(jsonPath("$.result.news[0].title").value("뉴스 제목"))
                .andExpect(jsonPath("$.result.news[0].originalLink").value("https://example.com/original"))
                .andExpect(jsonPath("$.result.news[0].naverLink").value("https://example.com/naver"));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        NewsQueryService newsQueryService() {
            return Mockito.mock(NewsQueryService.class);
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
