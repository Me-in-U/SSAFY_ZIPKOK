package com.ssafy.house.api.member.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.ssafy.house.api.member.dto.response.MemberProfileResponse;
import com.ssafy.house.api.member.service.MemberProfileService;
import com.ssafy.house.api.member.service.MemberQueryService;
import com.ssafy.house.global.common.exception.GlobalExceptionHandler;
import com.ssafy.house.global.exception.CurrentPasswordMismatchException;
import com.ssafy.house.global.security.config.SecurityConfig;
import com.ssafy.house.global.security.jwt.JwtUtil;

@WebMvcTest(controllers = MemberController.class, excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class))
@AutoConfigureMockMvc(addFilters = false)
@Import({MemberControllerTest.MockConfig.class, GlobalExceptionHandler.class})
@ImportAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberProfileService memberProfileService;

    @Autowired
    private MemberQueryService memberQueryService;

    @Test
    void getMyProfile_returnsSuccessBaseResponse() throws Exception {
        when(memberProfileService.getCurrentMember("tester@example.com")).thenReturn(
                new MemberProfileResponse(1, "테스터", "tester@example.com"));

        mockMvc.perform(get("/v1/member/me")
                .principal(new UsernamePasswordAuthenticationToken("tester@example.com", "N/A")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.email").value("tester@example.com"));
    }

    @Test
    void getMemberByEmail_returnsNotFoundWhenMemberMissing() throws Exception {
        when(memberQueryService.getMemberByEmail("missing@example.com"))
                .thenThrow(new java.util.NoSuchElementException("회원 정보를 찾을 수 없습니다."));

        mockMvc.perform(get("/v1/member/missing@example.com")
                .principal(new UsernamePasswordAuthenticationToken("admin@example.com", "N/A")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("회원 정보를 찾을 수 없습니다."));
    }

    @Test
    void updateMyProfile_returnsForbiddenWhenCurrentPasswordMismatches() throws Exception {
        when(memberProfileService.updateCurrentMember(Mockito.eq("tester@example.com"), Mockito.any()))
                .thenThrow(new CurrentPasswordMismatchException("현재 비밀번호가 일치하지 않습니다."));

        mockMvc.perform(put("/v1/member/me")
                .principal(new UsernamePasswordAuthenticationToken("tester@example.com", "N/A"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "새이름",
                          "currentPassword": "wrong-password",
                          "newPassword": "new-password"
                        }
                        """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("현재 비밀번호가 일치하지 않습니다."));
    }

    @Test
    void getMemberByEmail_returnsSuccessBaseResponse() throws Exception {
        when(memberQueryService.getMemberByEmail("tester@example.com")).thenReturn(
                new MemberProfileResponse(1, "테스터", "tester@example.com"));

        mockMvc.perform(get("/v1/member/tester@example.com")
                .principal(new UsernamePasswordAuthenticationToken("admin@example.com", "N/A")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.email").value("tester@example.com"));
    }

    @Test
    void deleteMember_returnsSuccessBaseResponse() throws Exception {
        mockMvc.perform(delete("/v1/member/7")
                .principal(new UsernamePasswordAuthenticationToken("admin@example.com", "N/A")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(200));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        MemberProfileService memberProfileService() {
            return Mockito.mock(MemberProfileService.class);
        }

        @Bean
        MemberQueryService memberQueryService() {
            return Mockito.mock(MemberQueryService.class);
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
