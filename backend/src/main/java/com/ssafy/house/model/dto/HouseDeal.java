package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseDeal {
    private Integer dealId;
    private String aptSeq;
    private String listingName;
    private String dealType;
    private long price;
    private String propertyType;
    private String spec;
    private String description;
    private String confirmedAt;
    private long deposit;
    private Integer monthlyRent;
}