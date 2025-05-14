package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 주택 거래 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseDeals {
    // Auto-increment PK
    private Integer no;
    private String aptSeq;
    private String aptDong;
    private String floor;
    private Integer dealYear;
    private Integer dealMonth;
    private Integer dealDay;
    private Integer excluUseAr;
    private String dealAmount;
}