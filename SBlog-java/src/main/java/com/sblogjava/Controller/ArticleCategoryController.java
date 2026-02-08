package com.sblogjava.Controller;

import com.sblogjava.Dto.CategoryDto;
import com.sblogjava.common.Result;
import com.sblogjava.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台 - 分类接口
 * 不需要登录，允许访客访问
 */
@RestController
@RequestMapping("/api/categories")
public class ArticleCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 前台 - 获取所有分类
     * 不需要登录
     */
    @GetMapping
    public Result<List<CategoryDto>> list() {
        return Result.success(categoryService.findAll());
    }

    /**
     * 前台 - 根据ID获取分类详情
     * 不需要登录
     */
    @GetMapping("/{id}")
    public Result<CategoryDto> getById(@PathVariable Long id) {
        try {
            return Result.success(categoryService.findById(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
