package com.ssafy.house.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int    commentId;
    private int    postId;
    private int    authorId;
    private String authorName;
    private String content;
    private Date   createdAt;
}
