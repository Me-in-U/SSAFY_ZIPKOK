package com.ssafy.house.restcontroller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.house.model.dto.News;
import com.ssafy.house.model.service.NewsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "https://ssafy.blog",
        "http://api.ssafy.blog",
        "http://192.168.204.108:5173",
        "http://172.22.16.1:5173",
        "http://localhost:8080"
}, allowedHeaders = "*", allowCredentials = "true", methods = { RequestMethod.GET, RequestMethod.OPTIONS })
@Tag(name = "NewsRestController", description = "크롤링된 뉴스 제공")
public class NewsRestController implements RestControllerHelper {
    private final NewsService newsService;
    private final Logger logger = LoggerFactory.getLogger(NewsRestController.class);

    @GetMapping("/latest")
    @Operation(summary = "최신 뉴스 조회", description = "크롤링된 뉴스 중 발행일 기준 최신 순으로 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> getLatest(
            @RequestParam(name = "limit", required = false, defaultValue = "20") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset) {
        try {
            List<News> list = newsService.getLatest(limit, offset);
            return handleSuccess(Map.of("news", list));
        } catch (DataAccessException e) {
            logger.error("뉴스 조회 중 DB 에러", e);
            return handleFail(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
