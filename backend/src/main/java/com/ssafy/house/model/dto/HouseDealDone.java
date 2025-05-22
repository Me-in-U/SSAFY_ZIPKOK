package com.ssafy.house.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseDealDone {
    private Integer no; // 거래번호
    private String aptSeq; // 아파트코드
    private String aptDong; // 아파트동
    private String floor; // 아파트층
    private Integer dealYear; // 거래년도
    private Integer dealMonth; // 거래월
    private Integer dealDay; // 거래일
    private BigDecimal excluUseAr; // 전용면적
    private String dealAmount; // 거래가격
}
