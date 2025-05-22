// src/main/java/com/ssafy/house/restcontroller/HouseInfoRestController.java
package com.ssafy.house.restcontroller;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.house.model.dto.HouseDealDone;
import com.ssafy.house.model.dto.HouseFullInfo;
import com.ssafy.house.model.dto.HouseInfo;
import com.ssafy.house.model.dto.SchoolInfo;
import com.ssafy.house.model.service.HouseDealsDoneService;
import com.ssafy.house.model.service.HouseInfoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/house")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "https://ssafy.blog",
        "http://api.ssafy.blog",
        "http://192.168.204.108:5173",
        "http://172.22.16.1:5173",
        "http://localhost:8080"
}, allowedHeaders = "*", allowCredentials = "true", methods = { RequestMethod.GET, RequestMethod.OPTIONS })
@RequiredArgsConstructor
public class HouseInfoRestController {
    private final HouseInfoService service;
    private final HouseDealsDoneService dealsService;

    // !단일 조회 (기존)
    @GetMapping("/{aptSeq}")
    public ResponseEntity<HouseInfo> getById(@PathVariable String aptSeq) throws Exception {
        HouseInfo info = service.getHouseInfo(aptSeq);
        return info != null
                ? ResponseEntity.ok(info)
                : ResponseEntity.notFound().build();
    }

    // !범위 조회 (지도 이동시 조회)
    @GetMapping("/search")
    public ResponseEntity<List<HouseInfo>> getByBounds(
            @RequestParam String minLat,
            @RequestParam String maxLat,
            @RequestParam String minLng,
            @RequestParam String maxLng) throws Exception {
        List<HouseInfo> list = service.getHouseInfoByBounds(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(list);
    }

    // 복수 조회 (GPT -> 검색결과 마커 or 즐겨찾기 마커 표시)
    @GetMapping("/batch")
    public ResponseEntity<List<HouseInfo>> getBySeqList(
            @RequestParam("seqs") String commaSeqs) throws Exception {
        List<String> seqList = Arrays.asList(commaSeqs.split(","));
        List<HouseInfo> list = service.getHouseInfoBySeqList(seqList);
        return ResponseEntity.ok(list);
    }

    // ! 상세 조회 용도
    /**
     * 6) 아파트 상세 정보 조회
     * house_info + house_detail + 대표이미지 + 최신 거래를 모두 묶어서 반환
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
     * school_detail 과 house_school 을 조인해 반환
     */
    @GetMapping("/{aptSeq}/schools")
    public ResponseEntity<List<SchoolInfo>> getSchools(@PathVariable String aptSeq) throws Exception {
        List<SchoolInfo> schools = service.getSchoolsByAptSeq(aptSeq);
        return ResponseEntity.ok(schools);
    }

    /**
     * 8) 지도 마커용 아파트 리스트 조회
     * 검색어 + 지역 + 거래유형 + 매물유형 + 가격범위 + 면적옵션 조합으로 조회
     */
    @GetMapping("/search/markers")
    public ResponseEntity<List<HouseInfo>> searchMarkers(
            @RequestParam Map<String, String> allParams) throws SQLException {
        List<HouseInfo> markers = service.getMarkerHouses(new HashMap<>(allParams));
        return ResponseEntity.ok(markers);
    }

    // !매매 완료 그래프 용도
    @GetMapping("/{aptSeq}/dealsDone")
    public ResponseEntity<List<HouseDealDone>> getDeals(@PathVariable String aptSeq) {
        List<HouseDealDone> deals = dealsService.getDealsByAptSeq(aptSeq);
        return ResponseEntity.ok(deals);
    }
}
