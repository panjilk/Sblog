package com.sblogjava.Dto;

import lombok.Data;

@Data
public class TagDto {
    private Long id;
    private String name;
    private Integer articleCount;
    private String createdAt;
}
