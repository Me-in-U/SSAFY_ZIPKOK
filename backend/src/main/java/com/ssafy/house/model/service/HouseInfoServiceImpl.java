package com.ssafy.house.model.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
    public HouseInfo getHouseInfo(String aptSeq) throws SQLException {
        return dao.selectHouseInfo(aptSeq);
    }

    @Override
    public List<HouseInfo> getHouseInfoBySeqList(List<String> aptSeqList) throws SQLException {
        if (aptSeqList == null || aptSeqList.isEmpty()) {
            return List.of();
        }
        return dao.selectBySeqList(aptSeqList);
    }

    @Override
    public List<HouseInfo> getHouseInfoByBounds(
            String minLat, String maxLat, String minLng, String maxLng) throws SQLException {
        return dao.selectByBounds(minLat, maxLat, minLng, maxLng);
    }

    @Override
    public List<String> searchByAptName(String partialName) throws SQLException {
        return dao.searchByAptName("%" + partialName + "%");
    }

    @Override
    public List<HouseInfo> searchByName(String partialName) throws SQLException {
        String keyword = "%" + partialName + "%";
        return dao.searchByName(keyword);
    }

    @Override
    public List<String> findBySido(String sido) throws SQLException {
        return dao.findBySido(sido);
    }

    @Override
    public List<String> findBySidoGugun(String sido, String gugun) throws SQLException {
        return dao.findBySidoGugun(sido, gugun);
    }

    @Override
    public List<String> findBySidoGugunDong(String sido, String gugun, String dong) throws SQLException {
        return dao.findBySidoGugunDong(sido, gugun, dong);
    }

    @Override
    public List<String> findByOptions(String sido, String gugun, String dong) throws SQLException {
        return dao.findByOptions(sido, gugun, dong);
    }

    @Override
    public List<String> findByOptionsAndAptName(
            String sido, String gugun, String dong, String aptNm) throws SQLException {
        return dao.findByOptionsAndAptName(sido, gugun, dong, aptNm);
    }

    @Override
    public HouseFullInfo getHouseFullInfo(String aptSeq) throws SQLException {
        return dao.selectHouseFullInfo(aptSeq);
    }

    @Override
    public List<HouseInfo> getMarkerHouses(Map<String, Object> params) throws SQLException {
        return dao.searchMarkersByOptionsAndName(params);
    }

    @Override
    public List<SchoolInfo> getSchoolsByAptSeq(String aptSeq) throws SQLException {
        return dao.selectSchoolsByAptSeq(aptSeq);
    }

    @Override
    public List<HouseInfo> searchFilter(String sido, String gugun, String dong, String aptNm) {
        return dao.searchFilter(sido, gugun, dong, aptNm);
    }
}
