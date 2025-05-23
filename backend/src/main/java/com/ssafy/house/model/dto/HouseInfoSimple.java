package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseInfoSimple {
    private String aptSeq;
    private String aptNm;
    private String latitude;
    private String longitude;
}
