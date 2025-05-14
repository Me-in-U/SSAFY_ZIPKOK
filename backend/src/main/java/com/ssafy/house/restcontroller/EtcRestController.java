package com.ssafy.house.restcontroller;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/etc")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "https://ssafy.blog",
        "http://192.168.204.108:5173/",
        "http://172.22.16.1:5173/", "http://localhost:8080/" })
@Tag(name = "EtcRestController", description = "기타 유틸리티성 기능 제공")
public class EtcRestController implements RestControllerHelper {

    private final RestTemplate restTemplate;
    @Value("${api.key_vworld}")
    private String vworldKey;

    @GetMapping("/sidos")
    @Operation(summary = "시/도조회", description = "국토교통부 디지털트윈 국도에서 시/도 정보 조회")
    private ResponseEntity<?> getSido() {
        try {
            StringBuilder url = new StringBuilder("https://api.vworld.kr/ned/data/admCodeList?");
            url.append("format=json&")
                    .append("numOfRows=10&domain=localhost&")
                    .append("key=").append(vworldKey);
            Map<String, Object> result = restTemplate.getForObject(new URI(url.toString()), Map.class);
            return handleSuccess(result);
        } catch (Exception e) {
            return handleFail(e);
        }
    }
}
