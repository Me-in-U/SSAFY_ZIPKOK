package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아파트 이전 거래 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseDealsDone {
    // PK
    private int no;
    private String aptSeq;
    private String aptDong; 
    private String floor;
    private int dealYear;
    private int dealMonth;
    private int dealDay;
    private double excluUseAr;
    private String dealAmount;
}