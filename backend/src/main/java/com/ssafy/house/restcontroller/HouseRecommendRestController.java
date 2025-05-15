// src/main/java/com/ssafy/house/restcontroller/HouseRestController.java
package com.ssafy.house.restcontroller;

import com.ssafy.house.model.dto.HouseAggregate;
import com.ssafy.house.model.service.HouseRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/house")
@CrossOrigin(origins = {
    "http://localhost:5173","http://localhost:5174",
    "https://ssafy.blog","https://api.ssafy.blog",
    "http://api.ssafy.blog","http://192.168.204.108:5173/",
    "http://172.22.16.1:5173/" })
public class HouseRecommendRestController {
    @Autowired
    private HouseRecommendService service;

    /** 최근 거래 매물 */
    @GetMapping("/recent")
    public ResponseEntity<List<HouseAggregate>> recent(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getRecentHouses(limit));
    }

    /** 최다 거래 매물 */
    @GetMapping("/most")
    public ResponseEntity<List<HouseAggregate>> most(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getMostTradedHouses(limit));
    }

    /** 종합 추천 매물 */
    @GetMapping("/recommended")
    public ResponseEntity<List<HouseAggregate>> recommended(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getRecommendedHouses(limit));
    }
}
