// src/main/java/com/ssafy/house/model/dto/HouseInfo.java
package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아파트 기본 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseInfo {
    private String  aptSeq;
    private String  sggCd;
    private String  umdCd;
    private String  umdNm;
    private String  jibun;
    private String  roadNmSggCd;
    private String  roadNm;
    private String  roadNmBonbun;
    private String  roadNmBubun;
    private String  aptNm;
    private Integer buildYear;
    private String  latitude;
    private String  longitude;
}
