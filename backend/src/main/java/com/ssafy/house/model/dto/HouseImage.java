package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아파트 이미지 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseImage {
    // PK
    private int id;
    private String aptSeq;
    private String imgPath;
    private String description;
}