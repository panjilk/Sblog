package com.sblogjava.service;

import com.sblogjava.Dto.CategoryDto;
import com.sblogjava.Dto.CategoryRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll();

    CategoryDto findById(Long id);

    void create(CategoryRequest request);

    void update(Long id, CategoryRequest request);

    void delete(Long id);
}
