package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아파트 상세 정보 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseInfoFull {
    private String aptSeq;
    private String aptNm;
    private String latitude;
    private String longitude;
    private String roadAddress;
    private String jibunAddress;
    private Integer buildYear;

    // 면적 범위
    private Double areaMin;
    private Double areaMax;

    // 거래·전세가 범위
    private Long tradePriceMin;
    private Long tradePriceMax;
    private Long jeonsePriceMin;
    private Long jeonsePriceMax;

    private String lastTradeDetail;
    private String imgPath;

    // 최신 거래 정보
    private String dealType; // 거래 유형 (월세, 전세, 매매)
    private Long deposit; // 보증금 (월세용)
    private Long monthlyRent; // 월세 금액 (월세용)
    private Long latestPrice; // 매매가 또는 보증금(전세)
    private String latestSpec;

    // 추가된 필드
    private String propertyType; // house_deal.property_type
    private String description; // house_deal.description
}
