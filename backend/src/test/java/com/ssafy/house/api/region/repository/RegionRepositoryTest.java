package com.ssafy.house.api.region.repository;

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

@ExtendWith(MockitoExtension.class)
class RegionRepositoryTest {

    @Mock
    private JdbcClient jdbcClient;

    @Mock
    private JdbcClient.StatementSpec statementSpec;

    @Mock
    private JdbcClient.MappedQuerySpec<String> mappedQuerySpec;

    @InjectMocks
    private RegionRepository regionRepository;

    @Test
    void getAllSido_usesJdbcClientQuery() {
        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.query(String.class)).thenReturn(mappedQuerySpec);
        when(mappedQuerySpec.list()).thenReturn(List.of("서울특별시", "부산광역시"));

        assertThat(regionRepository.getAllSido()).containsExactly("서울특별시", "부산광역시");
    }

    @Test
    void getGugunBySido_usesNamedParameter() {
        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("sidoName", "서울특별시")).thenReturn(statementSpec);
        when(statementSpec.query(String.class)).thenReturn(mappedQuerySpec);
        when(mappedQuerySpec.list()).thenReturn(List.of("강남구"));

        assertThat(regionRepository.getGugunBySido("서울특별시")).containsExactly("강남구");
    }

    @Test
    void getDongBySidoAndGugun_usesNamedParameters() {
        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.param("sidoName", "서울특별시")).thenReturn(statementSpec);
        when(statementSpec.param("gugunName", "강남구")).thenReturn(statementSpec);
        when(statementSpec.query(String.class)).thenReturn(mappedQuerySpec);
        when(mappedQuerySpec.list()).thenReturn(List.of("역삼동"));

        assertThat(regionRepository.getDongBySidoAndGugun("서울특별시", "강남구")).containsExactly("역삼동");
    }
}
