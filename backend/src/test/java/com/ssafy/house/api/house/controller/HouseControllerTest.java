package com.ssafy.house.api.house.controller;

import static org.mockito.Mockito.when;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.ssafy.house.api.house.dto.response.HouseDetailResponse;
import com.ssafy.house.api.house.dto.response.HouseInfoResponse;
import com.ssafy.house.api.house.dto.response.HouseSimpleListResponse;
import com.ssafy.house.api.house.dto.response.HouseSimpleResponse;
import com.ssafy.house.api.house.service.HouseQueryService;
import com.ssafy.house.global.common.exception.GlobalExceptionHandler;
import com.ssafy.house.global.security.config.SecurityConfig;
import com.ssafy.house.global.security.jwt.JwtUtil;

@WebMvcTest(controllers = HouseController.class, excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class))
@AutoConfigureMockMvc(addFilters = false)
@Import({HouseControllerTest.MockConfig.class, GlobalExceptionHandler.class})
@ImportAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
class HouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HouseQueryService houseQueryService;

    @Test
    void getHouse_returnsBaseResponse() throws Exception {
        when(houseQueryService.getHouse("123-456")).thenReturn(new HouseInfoResponse(
                "123-456", "11110", "10100", "반포동", "10-1", "11110", "반포대로", "10", "1",
                "반포자이", 2010, "37.000", "127.000"));

        mockMvc.perform(get("/v1/house/123-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.aptSeq").value("123-456"));
    }

    @Test
    void getHouse_returnsNotFoundWhenMissing() throws Exception {
        when(houseQueryService.getHouse("missing"))
                .thenThrow(new java.util.NoSuchElementException("아파트 정보를 찾을 수 없습니다."));

        mockMvc.perform(get("/v1/house/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("아파트 정보를 찾을 수 없습니다."));
    }

    @Test
    void getHouseDetail_returnsBaseResponse() throws Exception {
        when(houseQueryService.getHouseDetail("123-456")).thenReturn(new HouseDetailResponse(
                "123-456", "반포자이", "37.000", "127.000", "도로명", "지번", 2010,
                59.0, 84.0, 100000L, 150000L, 70000L, 90000L, "최근 거래", "/img.png",
                "매매", 0L, 0L, 120000L, "84A", "APT", "설명"));

        mockMvc.perform(get("/v1/house/123-456/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.aptNm").value("반포자이"))
                .andExpect(jsonPath("$.result.latestPrice").value(120000));
    }

    @Test
    void getHousesBySeqList_returnsBaseResponse() throws Exception {
        when(houseQueryService.getHousesBySeqList(List.of("123-456"))).thenReturn(
                new HouseSimpleListResponse(List.of(
                        new HouseSimpleResponse("123-456", "반포자이", "37.000", "127.000"))));

        mockMvc.perform(post("/v1/house/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "aptSeqList": ["123-456"]
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.houses[0].aptSeq").value("123-456"));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        HouseQueryService houseQueryService() {
            return Mockito.mock(HouseQueryService.class);
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
