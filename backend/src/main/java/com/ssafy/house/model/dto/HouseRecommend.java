package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseRecommend {
    private String aptSeq;
    private String imgPath;
    private String listingName;
    private Long   price;
    private String spec;
    private String propertyType;
    private String description;
}
