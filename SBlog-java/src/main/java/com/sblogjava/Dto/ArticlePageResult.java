package com.sblogjava.Dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticlePageResult {
    private Long total;
    private List<ArticleDto> list;
}
