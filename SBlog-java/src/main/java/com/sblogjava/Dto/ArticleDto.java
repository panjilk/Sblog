package com.sblogjava.Dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleDto {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String cover;
    private Long categoryId;
    private String categoryName;
    private List<String> tags;  // 标签名称（用于显示）
    private List<Long> tagIds;   // 标签ID（用于编辑）
    private String status;
    private Integer views;
    private Integer comments;
    private Boolean allowComment;
    private String createdAt;
    private String updatedAt;
}
