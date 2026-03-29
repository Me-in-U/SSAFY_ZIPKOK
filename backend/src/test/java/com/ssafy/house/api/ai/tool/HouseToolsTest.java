package com.ssafy.house.api.ai.tool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.house.api.house.dto.internal.HouseDealView;
import com.ssafy.house.api.house.repository.HouseRepository;

@ExtendWith(MockitoExtension.class)
class HouseToolsTest {

    @Mock
    private HouseRepository houseRepository;

    @InjectMocks
    private HouseTools houseTools;

    @Test
    void searchDealsByBudget_returnsRepositoryProjection() throws SQLException {
        HouseDealView deal =
                new HouseDealView(1, "1001", "반포자이", "매매", 120000L, "APT", "84A", "설명", "2026-03-29", 0L, 0);
        when(houseRepository.findDealsByBudget("서울", "서초", "반포동", "반포자이", 150000L))
                .thenReturn(List.of(deal));

        List<HouseDealView> result =
                houseTools.searchDealsByBudget("서울", "서초", "반포동", "반포자이", 150000L);

        assertThat(result).containsExactly(deal);
        verify(houseRepository).findDealsByBudget("서울", "서초", "반포동", "반포자이", 150000L);
    }

    @Test
    void searchHighestDeal_returnsRepositoryProjection() throws SQLException {
        HouseDealView deal =
                new HouseDealView(2, "2001", "잠실엘스", "전세", 90000L, "APT", "84B", "설명", "2026-03-28", 0L, 0);
        when(houseRepository.findHighestDeal("서울", "송파", "잠실동", "잠실엘스", "전세"))
                .thenReturn(deal);

        HouseDealView result = houseTools.searchHighestDeal("서울", "송파", "잠실동", "잠실엘스", "전세");

        assertThat(result).isEqualTo(deal);
        verify(houseRepository).findHighestDeal("서울", "송파", "잠실동", "잠실엘스", "전세");
    }
}

