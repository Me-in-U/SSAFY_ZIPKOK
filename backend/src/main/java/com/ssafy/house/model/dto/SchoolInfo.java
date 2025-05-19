// src/main/java/com/ssafy/house/model/dto/SchoolInfo.java
package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolInfo {
    private String schoolName;
    private String schoolType;
    private String distance;
}
