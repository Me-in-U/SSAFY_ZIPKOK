package com.ssafy.house.restcontroller;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.house.model.dto.HouseInfo;
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

    // 1) 단일 조회
    @GetMapping("/{aptSeq}")
    public ResponseEntity<HouseInfo> getById(@PathVariable String aptSeq) throws Exception {
        HouseInfo info = service.getHouseInfo(aptSeq);
        return info != null
                ? ResponseEntity.ok(info)
                : ResponseEntity.notFound().build();
    }

    // 2) 범위 조회
    @GetMapping("/search")
    public ResponseEntity<List<HouseInfo>> getByBounds(
            @RequestParam String minLat,
            @RequestParam String maxLat,
            @RequestParam String minLng,
            @RequestParam String maxLng) throws Exception {
        // DAO/Service/Mapper 모두 SELECT * → List<HouseInfo> 로 리턴하게 변경되어야 합니다
        List<HouseInfo> list = service.getHouseInfoByBounds(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(list);
    }

    // 3) 이름 일부 검색
    @GetMapping("/search/name")
    public ResponseEntity<List<String>> searchByName(
            @RequestParam String partialName) throws SQLException {
        return ResponseEntity.ok(
                service.searchByAptName(partialName));
    }

    /** 복수 apt_seq 를 comma-separated 로 받아서 List<HouseInfo> 반환 */
    @GetMapping("/batch")
    public ResponseEntity<List<HouseInfo>> getBySeqList(@RequestParam("seqs") String commaSeqs) throws SQLException {
        List<String> seqList = Arrays.asList(commaSeqs.split(","));
        List<HouseInfo> list = service.getHouseInfoBySeqList(seqList);
        return ResponseEntity.ok(list);
    }
}
