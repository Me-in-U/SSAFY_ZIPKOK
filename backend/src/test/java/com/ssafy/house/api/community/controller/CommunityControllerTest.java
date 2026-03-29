package com.ssafy.house.api.community.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.ssafy.house.api.community.dto.response.CommunityCommentResponse;
import com.ssafy.house.api.community.dto.response.CommunityPostDetailResponse;
import com.ssafy.house.api.community.dto.response.CommunityPostPageResponse;
import com.ssafy.house.api.community.dto.response.CommunityPostSummaryResponse;
import com.ssafy.house.api.community.service.CommunityApplicationService;
import com.ssafy.house.global.security.config.SecurityConfig;
import com.ssafy.house.global.security.jwt.JwtUtil;

@WebMvcTest(controllers = CommunityController.class, excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class))
@AutoConfigureMockMvc(addFilters = false)
@Import(CommunityControllerTest.MockConfig.class)
@ImportAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommunityApplicationService communityApplicationService;

    @Test
    void getPosts_returnsBaseResponse() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        when(communityApplicationService.getPosts("all", null, 0, 5)).thenReturn(
                new CommunityPostPageResponse(List.of(
                        new CommunityPostSummaryResponse(1, "apt", 3, "테스터", "제목", "내용", now, 5, 2)), 1));

        mockMvc.perform(get("/v1/community/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.posts[0].postId").value(1));
    }

    @Test
    void getPostDetail_returnsBaseResponse() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        when(communityApplicationService.getPostDetail(1)).thenReturn(new CommunityPostDetailResponse(
                1, "apt", 3, "테스터", "제목", "내용", now, 5, 1,
                List.of(new CommunityCommentResponse(10, 1, 7, "댓글러", "댓글 내용", now))));

        mockMvc.perform(get("/v1/community/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.comments[0].commentId").value(10));
    }

    @Test
    void createPost_returnsCreatedBaseResponse() throws Exception {
        mockMvc.perform(post("/v1/community/posts")
                .principal(new UsernamePasswordAuthenticationToken("tester@example.com", "N/A"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "categoryId": "apt",
                          "title": "제목",
                          "content": "내용"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(201));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        CommunityApplicationService communityApplicationService() {
            return Mockito.mock(CommunityApplicationService.class);
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
