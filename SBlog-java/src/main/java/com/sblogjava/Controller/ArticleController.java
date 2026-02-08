package com.sblogjava.Controller;

import com.sblogjava.Dto.ArticleDto;
import com.sblogjava.Dto.ArticlePageResult;
import com.sblogjava.Dto.ArticleRequest;
import com.sblogjava.common.Result;
import com.sblogjava.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public Result<ArticlePageResult> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return Result.success(articleService.getArticles(page, pageSize, status, keyword));
    }

    @GetMapping("/{id}")
    public Result<ArticleDto> getById(@PathVariable Long id) {
        try {
            return Result.success(articleService.getById(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Void> create(@RequestBody ArticleRequest request) {
        try {
            articleService.create(request);
            return Result.success("创建成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ArticleRequest request) {
        try {
            articleService.update(id, request);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            articleService.delete(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/views")
    public Result<Void> setViews(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer views = request.get("views");
            if (views == null) {
                return Result.error("浏览量不能为空");
            }
            articleService.setViews(id, views);
            return Result.success("设置成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
