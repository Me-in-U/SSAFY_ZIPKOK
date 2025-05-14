package com.ssafy.house.model.dto;

import lombok.Data;

@Data
public class SearchCondition {
    // 검색에 사용할 조건
    private String key;
    private String word;
    private int currentPage = 1;
    private int itemsPerPage = 5;

    public SearchCondition() {
    }

    public SearchCondition(String key, String word, int currentPage) {
        this.key = key;
        this.word = word;
        this.currentPage = currentPage;

    }

    /**
     * 페이징을 위해 현재 페이지의 offset 반환
     * 
     * @return
     */
    public int getOffset() {
        return (currentPage - 1) * itemsPerPage;
    }

    /**
     * SearchCondition에 key와 word가 모두 설정되어있는지 확인
     * 
     * @param condition
     * @return
     */
    public boolean hasKeyword() {
        return key != null && !key.isBlank() && word != null && !word.isBlank();
    }

}
