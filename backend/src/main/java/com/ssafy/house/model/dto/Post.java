package com.ssafy.house.model.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private int    postId;
    private String categoryId;
    private int    authorId;
    private String authorName;      
    private String title;
    private String content;
    private Timestamp createdAt;
    private int    views;
    private List<Comment> comments;  // 상세조회 시 주입
    private int commentCount;
}
