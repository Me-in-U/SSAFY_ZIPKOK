package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 아파트 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseDeal {
    // Auto-increment PK
    private Integer dealId;
    private String aptSeq;
    private String listingName;
    private String tradeType;
    private long price;
    private String propertyType;
    private String spec;
    private String description;
    private String confirmedAt;
    private long deposit;
    private Integer monthlyRent;
}