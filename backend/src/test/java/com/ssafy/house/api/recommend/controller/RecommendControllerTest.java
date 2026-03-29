package com.ssafy.house.api.recommend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.ssafy.house.api.recommend.dto.response.RecommendPropertyListResponse;
import com.ssafy.house.api.recommend.dto.response.RecommendPropertyResponse;
import com.ssafy.house.api.recommend.service.RecommendQueryService;
import com.ssafy.house.global.security.config.SecurityConfig;
import com.ssafy.house.global.security.jwt.JwtUtil;

@WebMvcTest(controllers = RecommendController.class, excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class))
@AutoConfigureMockMvc(addFilters = false)
@Import(RecommendControllerTest.MockConfig.class)
@ImportAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
class RecommendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecommendQueryService recommendQueryService;

    @Test
    void getRecentProperties_returnsBaseResponse() throws Exception {
        when(recommendQueryService.getRecentProperties(6)).thenReturn(new RecommendPropertyListResponse(List.of(
                new RecommendPropertyResponse("123-456", "/img.png", "반포자이", 100000L, "84A", "APT", "설명"))));

        mockMvc.perform(get("/v1/recommend/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.properties[0].aptSeq").value("123-456"));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        RecommendQueryService recommendQueryService() {
            return Mockito.mock(RecommendQueryService.class);
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
