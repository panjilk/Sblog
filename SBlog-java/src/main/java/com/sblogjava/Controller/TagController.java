package com.sblogjava.Controller;

import com.sblogjava.Dto.TagDto;
import com.sblogjava.Dto.TagRequest;
import com.sblogjava.common.Result;
import com.sblogjava.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public Result<List<TagDto>> list() {
        return Result.success(tagService.findAll());
    }

    @GetMapping("/{id}")
    public Result<TagDto> getById(@PathVariable Long id) {
        try {
            return Result.success(tagService.findById(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Void> create(@RequestBody TagRequest request) {
        try {
            tagService.create(request);
            return Result.success("创建成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody TagRequest request) {
        try {
            tagService.update(id, request);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            tagService.delete(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
