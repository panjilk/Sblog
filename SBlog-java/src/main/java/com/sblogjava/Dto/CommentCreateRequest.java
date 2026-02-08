package com.sblogjava.Dto;

import lombok.Data;

@Data
public class CommentCreateRequest {
    private String author;
    private String content;
    private String email;
    private Long articleId;
}
