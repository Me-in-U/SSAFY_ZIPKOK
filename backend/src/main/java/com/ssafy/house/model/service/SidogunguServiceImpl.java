package com.ssafy.house.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.ssafy.house.model.dao.SidogunguDao;
import com.ssafy.house.model.dto.Dong;
import com.ssafy.house.model.dto.Gugun;
import com.ssafy.house.model.dto.Sido;

@Service
@RequiredArgsConstructor

public class SidogunguServiceImpl implements SidogunguService {
    private final SidogunguDao sidogunduDao;

    @Override
    public List<String> getAllSido() {
        return sidogunduDao.selectDistinctSido();
    }

    @Override
    public List<String> getGugunBySido(String sidoName) {
        return sidogunduDao.selectDistinctGugun(sidoName);
    }

    @Override
    public List<String> getDongBySidoAndGugun(String sidoName, String gugunName) {
        Map<String, String> params = new HashMap<>();
        params.put("sidoName", sidoName);
        params.put("gugunName", gugunName);
        return sidogunduDao.selectDistinctDong(params);
    }
}
