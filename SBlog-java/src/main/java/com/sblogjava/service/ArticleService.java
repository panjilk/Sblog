package com.sblogjava.service;

import com.sblogjava.Dto.ArticleDto;
import com.sblogjava.Dto.ArticlePageResult;
import com.sblogjava.Dto.ArticleRequest;
import com.sblogjava.dao.Article;

public interface ArticleService {
    ArticlePageResult getArticles(Integer page, Integer pageSize, String status, String keyword);

    // 前台：按分类和/或标签筛选文章
    ArticlePageResult getArticlesByCategoryAndTag(Integer page, Integer pageSize, String status, Long categoryId, Long tagId);

    ArticleDto getById(Long id);

    void incrementViews(Long id);

    void setViews(Long id, Integer views);

    void updateCommentCount(Long id, int delta);

    void create(ArticleRequest request);

    void update(Long id, ArticleRequest request);

    void delete(Long id);
}
