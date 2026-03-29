package com.ssafy.house.api.recommend.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.ssafy.house.api.recommend.dto.internal.RecommendProperty;

@ExtendWith(MockitoExtension.class)
class RecommendRepositoryTest {

    @Mock
    private JdbcClient jdbcClient;

    @Mock
    private JdbcClient.StatementSpec statementSpec;

    @Mock
    private JdbcClient.MappedQuerySpec<RecommendProperty> mappedQuerySpec;

    @InjectMocks
    private RecommendRepository recommendRepository;

    @Test
    void getRecentProperties_usesJdbcClientQuery() {
        List<RecommendProperty> expected = List.of(
                new RecommendProperty("123-456", "/img.png", "반포자이", 100000L, "84A", "APT", "설명"));
        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("limit", 6)).thenReturn(statementSpec);
        when(statementSpec.query(RecommendProperty.class)).thenReturn(mappedQuerySpec);
        when(mappedQuerySpec.list()).thenReturn(expected);

        assertThat(recommendRepository.getRecentProperties(6)).isEqualTo(expected);
    }

    @Test
    void getNearStationProperties_usesJdbcClientQuery() {
        List<RecommendProperty> expected = List.of(
                new RecommendProperty("123-456", "/img.png", "반포자이", 100000L, "84A", "APT", "설명"));
        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("limit", 6)).thenReturn(statementSpec);
        when(statementSpec.query(RecommendProperty.class)).thenReturn(mappedQuerySpec);
        when(mappedQuerySpec.list()).thenReturn(expected);

        assertThat(recommendRepository.getNearStationProperties(6)).isEqualTo(expected);
    }

    @Test
    void getNewlywedsProperties_usesJdbcClientQuery() {
        List<RecommendProperty> expected = List.of(
                new RecommendProperty("123-456", "/img.png", "반포자이", 100000L, "84A", "APT", "설명"));
        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("limit", 6)).thenReturn(statementSpec);
        when(statementSpec.query(RecommendProperty.class)).thenReturn(mappedQuerySpec);
        when(mappedQuerySpec.list()).thenReturn(expected);

        assertThat(recommendRepository.getNewlywedsProperties(6)).isEqualTo(expected);
    }
}
