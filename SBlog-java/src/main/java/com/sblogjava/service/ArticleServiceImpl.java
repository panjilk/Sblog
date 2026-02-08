package com.sblogjava.service;

import com.sblogjava.Dto.ArticleDto;
import com.sblogjava.Dto.ArticlePageResult;
import com.sblogjava.Dto.ArticleRequest;
import com.sblogjava.dao.Article;
import com.sblogjava.dao.ArticleRepository;
import com.sblogjava.dao.Category;
import com.sblogjava.dao.CategoryRepository;
import com.sblogjava.dao.Tag;
import com.sblogjava.dao.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ArticlePageResult getArticles(Integer page, Integer pageSize, String status, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<Article> articlePage;
        Article.ArticleStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            statusEnum = Article.ArticleStatus.valueOf(status.toUpperCase());
        }

        if ((keyword == null || keyword.isEmpty()) && statusEnum == null) {
            articlePage = articleRepository.findAll(pageable);
        } else {
            articlePage = articleRepository.searchArticles(keyword, statusEnum, pageable);
        }

        ArticlePageResult result = new ArticlePageResult();
        result.setTotal(articlePage.getTotalElements());
        result.setList(articlePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    public ArticlePageResult getArticlesByCategoryAndTag(Integer page, Integer pageSize, String status, Long categoryId, Long tagId) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Article.ArticleStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            statusEnum = Article.ArticleStatus.valueOf(status.toUpperCase());
        }

        Page<Article> articlePage = articleRepository.searchByCategoryAndTag(statusEnum, categoryId, tagId, pageable);

        ArticlePageResult result = new ArticlePageResult();
        result.setTotal(articlePage.getTotalElements());
        result.setList(articlePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    public ArticleDto getById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        return convertToDto(article);
    }

    @Override
    @Transactional
    public void create(ArticleRequest request) {
        // 验证必填字段
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("文章标题不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("文章内容不能为空");
        }
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new RuntimeException("文章状态不能为空");
        }

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());
        article.setCover(request.getCover());
        article.setStatus(Article.ArticleStatus.valueOf(request.getStatus().toUpperCase()));
        article.setAllowComment(request.getAllowComment() != null ? request.getAllowComment() : true);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            article.setCategory(category);
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            article.setTags(tags);
        }

        articleRepository.save(article);
    }

    @Override
    @Transactional
    public void update(Long id, ArticleRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        // 验证必填字段
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("文章标题不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("文章内容不能为空");
        }
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new RuntimeException("文章状态不能为空");
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());
        article.setCover(request.getCover());
        article.setStatus(Article.ArticleStatus.valueOf(request.getStatus().toUpperCase()));
        article.setAllowComment(request.getAllowComment());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            article.setCategory(category);
        } else {
            // 如果 categoryId 为 null，清除原有分类
            article.setCategory(null);
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            article.setTags(tags);
        } else {
            // 如果 tagIds 为 null 或空，清除原有标签
            article.setTags(new java.util.ArrayList<>());
        }

        articleRepository.save(article);
    }

    @Override
    @Transactional
    public void incrementViews(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        article.setViews(article.getViews() + 1);
        articleRepository.save(article);
    }

    @Override
    @Transactional
    public void setViews(Long id, Integer views) {
        if (views == null || views < 0) {
            throw new RuntimeException("浏览量不能为负数");
        }
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        article.setViews(views);
        articleRepository.save(article);
    }

    @Override
    @Transactional
    public void updateCommentCount(Long id, int delta) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        int newCount = Math.max(0, article.getComments() + delta);
        article.setComments(newCount);
        articleRepository.save(article);
    }

    @Override
    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new RuntimeException("文章不存在");
        }
        articleRepository.deleteById(id);
    }

    private ArticleDto convertToDto(Article article) {
        ArticleDto dto = new ArticleDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setSummary(article.getSummary());
        dto.setCover(article.getCover());
        dto.setStatus(article.getStatus().name().toLowerCase());
        dto.setViews(article.getViews());
        dto.setComments(article.getComments());
        dto.setAllowComment(article.getAllowComment());
        dto.setCreatedAt(article.getCreatedAt() != null ?
                article.getCreatedAt().format(formatter) : null);
        dto.setUpdatedAt(article.getUpdatedAt() != null ?
                article.getUpdatedAt().format(formatter) : null);

        if (article.getCategory() != null) {
            dto.setCategoryId(article.getCategory().getId());
            dto.setCategoryName(article.getCategory().getName());
        }

        if (article.getTags() != null && !article.getTags().isEmpty()) {
            dto.setTags(article.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList()));
            dto.setTagIds(article.getTags().stream()
                    .map(Tag::getId)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
