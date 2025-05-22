package com.ssafy.house.restcontroller;

import com.ssafy.house.model.dto.HouseRecommend;
import com.ssafy.house.model.service.HouseRecommendService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/house/recommend")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "https://ssafy.blog",
        "https://api.ssafy.blog",
        "http://api.ssafy.blog",
        "http://192.168.204.108:5173/",
        "http://172.22.16.1:5173/"
})
@RequiredArgsConstructor
public class HouseRecommendRestController {
    private final HouseRecommendService recommendService;

    /**
     * GET /api/v1/house/recommend/recent?limit=10
     * 최근 거래 매물 상위 limit개 조회
     */
    @GetMapping("/recent")
    public ResponseEntity<List<HouseRecommend>> recent(
            @RequestParam(defaultValue = "6") int limit) {
        List<HouseRecommend> list = recommendService.getRecentProperties(limit);
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/v1/house/recommend/most?limit=10
     * 역세권 매물 상위 limit개 조회
     */
    @GetMapping("/nearstation")
    public ResponseEntity<List<HouseRecommend>> most(
            @RequestParam(defaultValue = "6") int limit) {
        List<HouseRecommend> list = recommendService.getNearStationProperties(limit);
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/v1/house/recommend/composite?limit=`0
     * 신혼 추천 매물 상위 limit개 조회
     */
    @GetMapping("/newlyweds")
    public ResponseEntity<List<HouseRecommend>> composite(
            @RequestParam(defaultValue = "6") int limit) {
        List<HouseRecommend> list = recommendService.getNewlywedsProperties(limit);
        return ResponseEntity.ok(list);
    }
}
