package com.sblogjava.Dto;

import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private Integer articleCount;
    private String createdAt;
}
