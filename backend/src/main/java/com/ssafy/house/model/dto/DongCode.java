package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 동코드 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DongCode {
    //PK
    private String dongCode;
    private String sidoName;
    private String gugunName;
    private String dongName;    
}