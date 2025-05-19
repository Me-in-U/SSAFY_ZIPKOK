package com.ssafy.house.model.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.house.model.dao.HouseInfoDao;
import com.ssafy.house.model.dto.HouseFullInfo;
import com.ssafy.house.model.dto.HouseInfo;
import com.ssafy.house.model.dto.SchoolInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HouseInfoServiceImpl implements HouseInfoService {
    private final HouseInfoDao dao;

    @Override
    public HouseInfo getHouseInfo(String aptSeq) throws Exception {
        return dao.selectHouseInfo(aptSeq);
    }

    @Override
    public List<HouseInfo> getHouseInfoBySeqList(List<String> aptSeqList) throws Exception {
        if (aptSeqList == null || aptSeqList.isEmpty()) {
            return List.of();
        }
        return dao.selectBySeqList(aptSeqList);
    }

    @Override
    public List<HouseInfo> getHouseInfoByBounds(
        String minLat, String maxLat, String minLng, String maxLng
    ) throws Exception {
        return dao.selectByBounds(minLat, maxLat, minLng, maxLng);
    }

    @Override
    public List<String> searchByAptName(String partialName) throws SQLException {
        return dao.searchByAptName("%" + partialName + "%");
    }

    @Override
    public List<String> findBySidoGugun(String sido, String gugun) throws Exception {
        return dao.findBySidoGugun(sido, gugun);
    }

    @Override
    public List<String> findBySidoGugunDong(String sido, String gugun, String dong) throws Exception {
        return dao.findBySidoGugunDong(sido, gugun, dong);
    }

    @Override
    public List<String> findByOptions(String sido, String gugun, String dong) throws Exception {
        return dao.findByOptions(sido, gugun, dong);
    }

    @Override
    public List<String> findByOptionsAndAptName(
        String sido, String gugun, String dong, String aptNm
    ) throws Exception {
        return dao.findByOptionsAndAptName(sido, gugun, dong, aptNm);
    }

    // ↓ 추가된 구현
    @Override
    public HouseFullInfo getHouseFullInfo(String aptSeq) throws Exception {
        return dao.selectHouseFullInfo(aptSeq);
    }

    @Override
    public List<SchoolInfo> getSchoolsByAptSeq(String aptSeq) throws Exception {
        return dao.selectSchoolsByAptSeq(aptSeq);
    }
}
