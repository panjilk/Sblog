package com.sblogjava.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByStatus(Article.ArticleStatus status, Pageable pageable);

    long countByStatus(Article.ArticleStatus status);

    // 统计总访问量
    @Query("SELECT COALESCE(SUM(a.views), 0) FROM Article a")
    Long sumViews();

    @Query("SELECT a FROM Article a WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR a.title LIKE %:keyword% OR a.content LIKE %:keyword%) " +
            "AND (:status IS NULL OR a.status = :status)")
    Page<Article> searchArticles(@Param("keyword") String keyword,
                                  @Param("status") Article.ArticleStatus status,
                                  Pageable pageable);

    // 前台：按分类和/或标签筛选文章
    @Query("SELECT a FROM Article a WHERE " +
            "(:status IS NULL OR a.status = :status) " +
            "AND (:categoryId IS NULL OR a.category.id = :categoryId) " +
            "AND (:tagId IS NULL OR EXISTS (SELECT t FROM a.tags t WHERE t.id = :tagId))")
    Page<Article> searchByCategoryAndTag(@Param("status") Article.ArticleStatus status,
                                          @Param("categoryId") Long categoryId,
                                          @Param("tagId") Long tagId,
                                          Pageable pageable);
}
