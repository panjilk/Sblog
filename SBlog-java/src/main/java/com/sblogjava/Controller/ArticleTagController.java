package com.sblogjava.Controller;

import com.sblogjava.Dto.TagDto;
import com.sblogjava.common.Result;
import com.sblogjava.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台 - 标签接口
 * 不需要登录，允许访客访问
 */
@RestController
@RequestMapping("/api/tags")
public class ArticleTagController {

    @Autowired
    private TagService tagService;

    /**
     * 前台 - 获取所有标签
     * 不需要登录
     */
    @GetMapping
    public Result<List<TagDto>> list() {
        return Result.success(tagService.findAll());
    }

    /**
     * 前台 - 根据ID获取标签详情
     * 不需要登录
     */
    @GetMapping("/{id}")
    public Result<TagDto> getById(@PathVariable Long id) {
        try {
            return Result.success(tagService.findById(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
