package com.ssafy.house.api.recommend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.house.api.recommend.repository.RecommendRepository;
import com.ssafy.house.api.recommend.dto.response.RecommendPropertyListResponse;
import com.ssafy.house.api.recommend.dto.internal.RecommendProperty;

@ExtendWith(MockitoExtension.class)
class RecommendQueryServiceTest {

    @Mock
    private RecommendRepository recommendRepository;

    @InjectMocks
    private RecommendQueryService recommendQueryService;

    @Test
    void getRecentProperties_mapsLegacyRecommendToResponse() {
        when(recommendRepository.getRecentProperties(3)).thenReturn(List.of(
                new RecommendProperty("123-456", "/img.png", "반포자이", 100000L, "84A", "APT", "설명")));

        RecommendPropertyListResponse response = recommendQueryService.getRecentProperties(3);

        assertThat(response.properties()).hasSize(1);
        assertThat(response.properties().getFirst().aptSeq()).isEqualTo("123-456");
        assertThat(response.properties().getFirst().listingName()).isEqualTo("반포자이");
    }
}

