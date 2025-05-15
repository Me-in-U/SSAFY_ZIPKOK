package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아파트 상세 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseDetail {
    // PK
    private String aptSeq;
    private double areaMin;
    private double areaMax;
    private long tradePriceMin;
    private long tradePriceMax;
    private long jeonsePriceMin;
    private long jeonsePriceMax;
    private String lastTradeDetail;
    private int schoolId;
}