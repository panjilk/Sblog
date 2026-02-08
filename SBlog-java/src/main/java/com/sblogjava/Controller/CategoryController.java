package com.sblogjava.Controller;

import com.sblogjava.Dto.CategoryDto;
import com.sblogjava.Dto.CategoryRequest;
import com.sblogjava.common.Result;
import com.sblogjava.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result<List<CategoryDto>> list() {
        return Result.success(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public Result<CategoryDto> getById(@PathVariable Long id) {
        try {
            return Result.success(categoryService.findById(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Void> create(@RequestBody CategoryRequest request) {
        try {
            categoryService.create(request);
            return Result.success("创建成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        try {
            categoryService.update(id, request);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
