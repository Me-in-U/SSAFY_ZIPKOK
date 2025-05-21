package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {
    private int id;
    private String title;
    private String originallink;
    private String naverlink;
    private String description;
    private LocalDateTime pubDate;
}