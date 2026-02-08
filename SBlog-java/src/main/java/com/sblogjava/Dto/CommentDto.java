package com.sblogjava.Dto;

import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private String author;
    private String email;
    private Long articleId;
    private String articleTitle;
    private String status;
    private String ip;
    private String createdAt;
}
