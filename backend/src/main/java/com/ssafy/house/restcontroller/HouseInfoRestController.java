package com.ssafy.house.restcontroller;

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

    // 1) 전체 조회: GET /house
    @GetMapping
    public ResponseEntity<List<HouseInfo>> getAll() throws Exception {
        List<HouseInfo> list = service.getAllHouseInfo();
        return ResponseEntity.ok(list);
    }

    // 2) 단일 조회: GET /house/{aptSeq}
    @GetMapping("/{aptSeq}")
    public ResponseEntity<HouseInfo> getById(@PathVariable String aptSeq) throws Exception {
        HouseInfo info = service.getHouseInfo(aptSeq);
        return info != null
                ? ResponseEntity.ok(info)
                : ResponseEntity.notFound().build();
    }

    // 3) 범위 조회: GET /house/search?minLat=...&maxLat=...&minLng=...&maxLng=...
    @GetMapping("/search")
    public ResponseEntity<List<HouseInfo>> getByBounds(
            @RequestParam String minLat,
            @RequestParam String maxLat,
            @RequestParam String minLng,
            @RequestParam String maxLng) throws Exception {
        List<HouseInfo> list = service.getHouseInfoByBounds(minLat, maxLat, minLng, maxLng);
        return ResponseEntity.ok(list);
    }

    // 4) 등록: POST /house
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody HouseInfo info) throws Exception {
        boolean created = service.addHouseInfo(info);
        return created
                ? ResponseEntity.status(201).build()
                : ResponseEntity.badRequest().build();
    }

    // 5) 수정: PUT /house/{aptSeq}
    @PutMapping("/{aptSeq}")
    public ResponseEntity<Void> update(
            @PathVariable String aptSeq,
            @RequestBody HouseInfo info) throws Exception {
        info.setAptSeq(aptSeq);
        boolean updated = service.modifyHouseInfo(info);
        return updated
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    // 6) 삭제: DELETE /house/{aptSeq}
    @DeleteMapping("/{aptSeq}")
    public ResponseEntity<Void> delete(@PathVariable String aptSeq) throws Exception {
        boolean removed = service.removeHouseInfo(aptSeq);
        return removed
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
