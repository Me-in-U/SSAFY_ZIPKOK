package com.ssafy.house.api.news.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.api.news.dto.response.NewsLatestResponse;
import com.ssafy.house.api.news.service.NewsQueryService;
import com.ssafy.house.global.common.base.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 신규 뉴스 조회 API를 제공하는 컨트롤러이다.
 */
@RestController
@RequestMapping("/v1/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "뉴스 조회 기능")
public class NewsController {

    private final NewsQueryService newsQueryService;

    /**
     * 최신 뉴스 목록을 조회한다.
     *
     * @param limit 조회 건수
     * @param offset 조회 시작 위치
     * @return 최신 뉴스 응답
     */
    @GetMapping("/latest")
    @Operation(summary = "최신 뉴스 조회", description = "발행일 기준 최신 뉴스 목록을 조회한다.")
    public BaseResponse<NewsLatestResponse> getLatestNews(
            @RequestParam(name = "limit", required = false, defaultValue = "20") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset) {
        return BaseResponse.onSuccess(newsQueryService.getLatestNews(limit, offset));
    }
}
