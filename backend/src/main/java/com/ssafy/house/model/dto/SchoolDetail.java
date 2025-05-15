package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 학교 상세 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDetail {
    // PK
    private int id;
    private String schoolName;      
    private String schoolType;
    private String address;
    private String phone;
    private String established;
    private String office;
    private String teacherCount;
    private String studentCount;
    private String homepage;
}