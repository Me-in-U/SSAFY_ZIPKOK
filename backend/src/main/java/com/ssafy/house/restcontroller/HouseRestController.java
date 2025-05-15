package com.ssafy.house.restcontroller;

import com.ssafy.house.model.dto.HouseAggregate;
import com.ssafy.house.model.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class HouseRestController {
    @Autowired
    private HouseService service;

    /**
     * 1) 단일 조회: GET /api/v1/house/{aptSeq}/detail
     *    아파트 고유번호로 HouseInfo, HouseDetail, Deals, Images, School 정보를 모두 묶어서 반환
     */
    @GetMapping("/{aptSeq}/detail")
    public ResponseEntity<HouseAggregate> getHouseAggregate(@PathVariable String aptSeq) throws Exception {
        HouseAggregate dto = service.getHouseAggregate(aptSeq);
        return dto != null
                ? ResponseEntity.ok(dto)
                : ResponseEntity.notFound().build();
    }
}
