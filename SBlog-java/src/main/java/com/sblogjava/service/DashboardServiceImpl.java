package com.sblogjava.service;

import com.sblogjava.Dto.DashboardDto;
import com.sblogjava.dao.Article;
import com.sblogjava.dao.ArticleRepository;
import com.sblogjava.dao.CommentRepository;
import com.sblogjava.dao.Message;
import com.sblogjava.dao.MessageRepository;
import com.sblogjava.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public DashboardDto getDashboard() {
        DashboardDto dashboard = new DashboardDto();

        // 统计数据
        dashboard.setArticleCount(articleRepository.count());
        dashboard.setDraftCount(articleRepository.countByStatus(Article.ArticleStatus.DRAFT));
        dashboard.setCommentCount(commentRepository.count());
        dashboard.setMessageCount(messageRepository.count());
        dashboard.setUserCount(userRepository.count());

        // 总访问量
        Long totalViews = articleRepository.sumViews();
        dashboard.setViewCount(totalViews != null ? totalViews : 0L);

        // 今日访问 - 统计今天更新过的文章的总访问量
        // 注意：这里只是简单的实现，如果要精确统计今日访问量，需要添加访问记录表
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<Article> todayUpdatedArticles = articleRepository.findAll().stream()
                .filter(a -> a.getUpdatedAt() != null &&
                        !a.getUpdatedAt().isBefore(startOfDay) &&
                        a.getUpdatedAt().isBefore(endOfDay))
                .collect(Collectors.toList());

        long todayViews = todayUpdatedArticles.stream()
                .mapToLong(Article::getViews)
                .sum();
        dashboard.setTodayViews(todayViews);

        // 最近文章
        List<Article> recentArticles = articleRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());

        List<DashboardDto.RecentArticle> recentArticleDtos = recentArticles.stream()
                .map(article -> {
                    DashboardDto.RecentArticle ra = new DashboardDto.RecentArticle();
                    ra.setId(article.getId());
                    ra.setTitle(article.getTitle());
                    ra.setViews(article.getViews());
                    ra.setDate(article.getCreatedAt() != null ?
                            article.getCreatedAt().format(formatter) : null);
                    return ra;
                })
                .collect(Collectors.toList());

        dashboard.setRecentArticles(recentArticleDtos);

        return dashboard;
    }
}
