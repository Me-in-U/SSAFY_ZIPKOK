package com.ssafy.house.model.service;

import java.util.List;

public interface SidogunguService {
    List<String> getAllSido();

    List<String> getGugunBySido(String sidoName);

    List<String> getDongBySidoAndGugun(String sidoName, String gugunName);
}
