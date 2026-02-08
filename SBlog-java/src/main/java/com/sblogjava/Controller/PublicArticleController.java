package com.sblogjava.Controller;

import com.sblogjava.Dto.ArticleDto;
import com.sblogjava.Dto.ArticlePageResult;
import com.sblogjava.common.Result;
import com.sblogjava.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 前台公开文章接口（不需要登录）
 */
@RestController
@RequestMapping("/api/articles")
public class PublicArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 获取已发布文章列表（支持搜索、分类筛选、标签筛选）
     * 前台首页、搜索页面使用
     */
    @GetMapping
    public Result<ArticlePageResult> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId) {
        // 固定只返回已发布的文章
        String status = "published";

        // 如果有分类或标签筛选，使用专门的筛选方法
        if (categoryId != null || tagId != null) {
            return Result.success(articleService.getArticlesByCategoryAndTag(page, pageSize, status, categoryId, tagId));
        }

        // 否则使用普通的搜索方法（只支持关键词搜索）
        return Result.success(articleService.getArticles(page, pageSize, status, keyword));
    }

    /**
     * 获取文章详情（前台）
     */
    @GetMapping("/{id}")
    public Result<ArticleDto> getById(@PathVariable Long id) {
        try {
            // 增加访问量
            articleService.incrementViews(id);
            ArticleDto article = articleService.getById(id);
            // 前台只能查看已发布的文章
            if (!"published".equals(article.getStatus())) {
                return Result.error("文章不存在");
            }
            return Result.success(article);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
