package com.ssafy.house.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Page<T> {
    private SearchCondition condition;// 검색 조건
    private int navSize = 5; // 네비게이션 사이즈
    private int totalItems; // 전체 항목 수
    private List<T> list; // 화면에서 보여줄 데이터

    private int totalPages; // 전체 페이지 수
    private boolean hasPre; // 이전 버튼 활성화 여부
    private boolean hasNext; // 다음 버튼 활성화 여부
    private int startPage; // 시작 페이지 번호
    private int endPage; // 끝 페이지 번호

    public Page(SearchCondition condition, int totalItems, List<T> list) {
        this.condition = condition;
        this.totalItems = totalItems;
        this.list = list;

        // 전체 페이지 수 계산
        this.totalPages = (int) Math.ceil((double) totalItems / condition.getItemsPerPage());

        // 시작 페이지와 끝 페이지 계산
        this.startPage = (condition.getCurrentPage() - 1) / navSize * navSize + 1;
        this.endPage = Math.min(startPage + navSize - 1, totalPages);

        // 이전/다음 버튼 활성화 여부
        this.hasPre = startPage != 1;
        this.hasNext = endPage < totalPages;
    }
}
