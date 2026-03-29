package com.ssafy.house.api.region.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.house.api.region.repository.RegionRepository;
import com.ssafy.house.api.region.dto.response.RegionNameListResponse;

@ExtendWith(MockitoExtension.class)
class RegionQueryServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionQueryService regionQueryService;

    @Test
    void getSidos_returnsResponseWrapper() {
        when(regionRepository.getAllSido()).thenReturn(List.of("서울특별시", "부산광역시"));

        RegionNameListResponse response = regionQueryService.getSidos();

        assertThat(response.items()).containsExactly("서울특별시", "부산광역시");
    }
}
