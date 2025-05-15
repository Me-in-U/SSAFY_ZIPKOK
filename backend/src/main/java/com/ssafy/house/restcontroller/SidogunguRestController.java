package com.ssafy.house.restcontroller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.house.model.service.SidogunguService;

@RestController
@RequestMapping("/api/v1/sidogungu")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "https://ssafy.blog",
        "https://api.ssafy.blog",
        "http://api.ssafy.blog",
        "http://192.168.204.108:5173/",
        "http://172.22.16.1:5173/" })
public class SidogunguRestController {

    private final SidogunguService sidogunguService;

    // 시도 전체
    @GetMapping("/sido")
    public ResponseEntity<List<String>> sido() {
        return ResponseEntity.ok(sidogunguService.getAllSido());
    }

    // 특정 시도의 구군
    @GetMapping("/gugun/{sidoName}")
    public ResponseEntity<List<String>> gugun(@PathVariable String sidoName) {
        return ResponseEntity.ok(sidogunguService.getGugunBySido(sidoName));
    }

    // 특정 구군의 읍면동
    @GetMapping("/dong/{sidoName}/{gugunName}")
    public ResponseEntity<List<String>> dong(@PathVariable String sidoName,
            @PathVariable String gugunName) {
        return ResponseEntity.ok(sidogunguService.getDongBySidoAndGugun(sidoName, gugunName));
    }
}