package com.ssafy.house.api.member.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.ssafy.house.api.member.dto.response.FavoriteSummaryResponse;
import com.ssafy.house.api.member.dto.response.MemberFavoriteCommandResponse;
import com.ssafy.house.api.member.dto.response.MemberFavoriteListResponse;
import com.ssafy.house.api.member.service.MemberFavoriteService;
import com.ssafy.house.global.security.config.SecurityConfig;
import com.ssafy.house.global.security.jwt.JwtUtil;

@WebMvcTest(controllers = MemberFavoriteController.class, excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class))
@AutoConfigureMockMvc(addFilters = false)
@Import(MemberFavoriteControllerTest.MockConfig.class)
@ImportAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
class MemberFavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberFavoriteService memberFavoriteService;

    @Test
    void getFavorites_returnsSuccessBaseResponse() throws Exception {
        when(memberFavoriteService.getFavorites("tester@example.com")).thenReturn(
                new MemberFavoriteListResponse(List.of(
                        new FavoriteSummaryResponse(
                                "123-456", "/img.png", "반포자이", 120000L, "84A", "APT", "설명"))));

        mockMvc.perform(get("/v1/member/me/favorites")
                .principal(new UsernamePasswordAuthenticationToken("tester@example.com", "N/A")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.favorites[0].aptSeq").value("123-456"))
                .andExpect(jsonPath("$.result.favorites[0].listingName").value("반포자이"));
    }

    @Test
    void addFavorite_returnsSuccessBaseResponse() throws Exception {
        when(memberFavoriteService.addFavorite("tester@example.com", "123-456"))
                .thenReturn(new MemberFavoriteCommandResponse("123-456"));

        mockMvc.perform(post("/v1/member/me/favorites/123-456")
                .principal(new UsernamePasswordAuthenticationToken("tester@example.com", "N/A")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.aptSeq").value("123-456"));
    }

    @Test
    void removeFavorite_returnsSuccessBaseResponse() throws Exception {
        when(memberFavoriteService.removeFavorite("tester@example.com", "123-456"))
                .thenReturn(new MemberFavoriteCommandResponse("123-456"));

        mockMvc.perform(delete("/v1/member/me/favorites/123-456")
                .principal(new UsernamePasswordAuthenticationToken("tester@example.com", "N/A")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.aptSeq").value("123-456"));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        MemberFavoriteService memberFavoriteService() {
            return Mockito.mock(MemberFavoriteService.class);
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
