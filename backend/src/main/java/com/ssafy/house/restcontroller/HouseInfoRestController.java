// src/main/java/com/ssafy/house/restcontroller/HouseInfoRestController.java
package com.ssafy.house.restcontroller;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.house.model.dto.HouseFullInfo;
import com.ssafy.house.model.dto.HouseInfo;
import com.ssafy.house.model.dto.SchoolInfo;
import com.ssafy.house.model.service.HouseInfoService;

@RestController
@RequestMapping("/api/v1/house")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "https://ssafy.blog",
        "https://api.ssafy.blog",
        "http://api.ssafy.blog",
        "http://192.168.204.108:5173/",
        "http://172.22.16.1:5173/" })
public class HouseInfoRestController {

    @Autowired
    private HouseInfoService service;

    // 1) 단일 조회 (기존)
    @GetMapping("/{aptSeq}")
    public ResponseEntity<HouseInfo> getById(@PathVariable String aptSeq) throws Exception {
        HouseInfo info = service.getHouseInfo(aptSeq);
        return info != null
                ? ResponseEntity.ok(info)
                : ResponseEntity.notFound().build();
    }

    // 2) 범위 조회 (기존)
    @GetMapping("/search")
    public ResponseEntity<List<HouseInfo>> getByBounds(
            @RequestParam String minLat,
            @RequestParam String maxLat,
            @RequestParam String minLng,
            @RequestParam String maxLng) throws Exception {
        List<HouseInfo> list = service.getHouseInfoByBounds(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(list);
    }

    // 3-1) 이름 일부 검색 (챗봇용)
    @GetMapping("/search/name")
    public ResponseEntity<List<String>> searchByAptName(
            @RequestParam String partialName) throws SQLException {
        return ResponseEntity.ok(service.searchByAptName(partialName));
    }
    // 3-2) 이름 일부 검색 (검색창용)
    @GetMapping("/search/name/apt")
    public ResponseEntity<List<HouseInfo>> searchByName(@RequestParam String partialName) throws SQLException {
    return ResponseEntity.ok(service.searchByName(partialName));
    }
    // 4) 복수 조회 (기존)
    @GetMapping("/batch")
    public ResponseEntity<List<HouseInfo>> getBySeqList(
            @RequestParam("seqs") String commaSeqs) throws Exception {
        List<String> seqList = Arrays.asList(commaSeqs.split(","));
        List<HouseInfo> list = service.getHouseInfoBySeqList(seqList);
        return ResponseEntity.ok(list);
    }
    /**
     *  5) 검색어 + 상세 필터(지역, 매물유형, 거래유형, 가격범위, 면적옵션, 준공년도) 조합 조회
     */
    @GetMapping("/search/full")
    public ResponseEntity<List<HouseFullInfo>> searchFull(
        @RequestParam(required = false) String partialName,
        @RequestParam(required = false) String sido,
        @RequestParam(required = false) String gugun,
        @RequestParam(required = false) String dong,
        @RequestParam(required = false) String propertyType,
        @RequestParam(required = false) String dealType,
        @RequestParam(required = false) Integer builtYear,
        @RequestParam(required = false) Long minPrice,
        @RequestParam(required = false) Long maxPrice,
        @RequestParam(required = false) String areaOption
    ) throws SQLException {
        List<HouseFullInfo> list = service.searchFullInfoByOptionsAndName(
            partialName, sido, gugun, dong,
            propertyType, dealType,
            builtYear, minPrice, maxPrice,
            areaOption
        );
        return ResponseEntity.ok(list);
    }

    /**
     * 6) 아파트 상세 정보 조회
     *    house_info + house_detail + 대표이미지 + 최신 거래를 모두 묶어서 반환
     */
    @GetMapping("/{aptSeq}/detail")
    public ResponseEntity<HouseFullInfo> getFullInfo(@PathVariable String aptSeq) throws Exception {
        HouseFullInfo full = service.getHouseFullInfo(aptSeq);
        return full != null
                ? ResponseEntity.ok(full)
                : ResponseEntity.notFound().build();
    }

    /**
     * 7) 해당 아파트의 학교 리스트 조회
     *    school_detail 과 house_school 을 조인해 반환
     */
    @GetMapping("/{aptSeq}/schools")
    public ResponseEntity<List<SchoolInfo>> getSchools(@PathVariable String aptSeq) throws Exception {
        List<SchoolInfo> schools = service.getSchoolsByAptSeq(aptSeq);
        return ResponseEntity.ok(schools);
    }
}
