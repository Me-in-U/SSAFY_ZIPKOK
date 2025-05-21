package com.ssafy.house.model.service;

import com.ssafy.house.model.dto.News;

import java.util.List;

public interface NewsService {
    /**
     * 발행일 기준으로 최신 뉴스 목록을 limit 개수만큼 조회
     * 
     * @param limit  조회할 뉴스 개수
     * @param offset 조회할 뉴스 시작 위치
     * @return News DTO 리스트
     */
    List<News> getLatest(int limit, int offset);
}