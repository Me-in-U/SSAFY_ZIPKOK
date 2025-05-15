package com.ssafy.house.model.service;

import java.util.List;

import com.ssafy.house.model.dto.Dong;
import com.ssafy.house.model.dto.Gugun;
import com.ssafy.house.model.dto.Sido;

public interface SidogunguService {
    List<String> getAllSido();

    List<String> getGugunBySido(String sidoName);

    List<String> getDongBySidoAndGugun(String sidoName, String gugunName);
}
