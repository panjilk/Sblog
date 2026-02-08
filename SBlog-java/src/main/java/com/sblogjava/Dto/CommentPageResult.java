package com.sblogjava.Dto;

import lombok.Data;

import java.util.List;

@Data
public class CommentPageResult {
    private Long total;
    private List<CommentDto> list;
}
