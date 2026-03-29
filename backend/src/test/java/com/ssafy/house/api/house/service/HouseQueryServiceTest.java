package com.ssafy.house.api.house.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.house.api.house.repository.HouseRepository;
import com.ssafy.house.api.house.dto.internal.HouseDetailView;
import com.ssafy.house.api.house.dto.internal.HouseSimpleView;
import com.ssafy.house.api.house.dto.response.HouseDetailResponse;
import com.ssafy.house.api.house.dto.response.HouseSimpleListResponse;

@ExtendWith(MockitoExtension.class)
class HouseQueryServiceTest {

    @Mock
    private HouseRepository houseRepository;

    @InjectMocks
    private HouseQueryService houseQueryService;

    @Test
    void getHousesBySeqList_mapsLegacySimpleInfoToResponse() throws SQLException {
        when(houseRepository.getHouseInfoBySeqList(List.of("123-456"))).thenReturn(List.of(
                new HouseSimpleView("123-456", "반포자이", "37.000", "127.000")));

        HouseSimpleListResponse response = houseQueryService.getHousesBySeqList(List.of("123-456"));

        assertThat(response.houses()).hasSize(1);
        assertThat(response.houses().getFirst().aptSeq()).isEqualTo("123-456");
        assertThat(response.houses().getFirst().aptNm()).isEqualTo("반포자이");
    }

    @Test
    void getHouseDetail_mapsLegacyDetailToResponse() throws SQLException {
        when(houseRepository.getHouseInfoFull("123-456")).thenReturn(new HouseDetailView(
                "123-456", "반포자이", "37.000", "127.000", "도로명", "지번", 2010,
                59.0, 84.0, 100000L, 150000L, 70000L, 90000L, "최근 거래", "/img.png",
                "매매", 0L, 0L, 120000L, "84A", "APT", "설명"));

        HouseDetailResponse response = houseQueryService.getHouseDetail("123-456");

        assertThat(response.aptSeq()).isEqualTo("123-456");
        assertThat(response.aptNm()).isEqualTo("반포자이");
        assertThat(response.latestPrice()).isEqualTo(120000L);
        assertThat(response.propertyType()).isEqualTo("APT");
    }
}

