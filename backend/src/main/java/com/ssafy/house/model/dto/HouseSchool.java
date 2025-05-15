package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 학군 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseSchool {
    // PK
    private String aptSeq;
    private int schoolId;
    private String district;
    private String assignmentDetail;
    private String distance;
}