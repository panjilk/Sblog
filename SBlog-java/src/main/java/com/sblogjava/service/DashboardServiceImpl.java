package com.sblogjava.service;

import com.sblogjava.Dto.DashboardDto;
import com.sblogjava.Dto.DashboardChartData;
import com.sblogjava.dao.Article;
import com.sblogjava.dao.ArticleRepository;
import com.sblogjava.dao.Category;
import com.sblogjava.dao.CommentRepository;
import com.sblogjava.dao.Message;
import com.sblogjava.dao.MessageRepository;
import com.sblogjava.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Override
    public DashboardChartData getChartData(int days) {
        DashboardChartData chartData = new DashboardChartData();

        // 1. 获取趋势数据
        List<DashboardChartData.TrendData> trendDataList = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            DashboardChartData.TrendData trendData = new DashboardChartData.TrendData();
            trendData.setDate(date.format(dateFormatter));

            // 当天发布的文章数量
            long articleCount = articleRepository.findAll().stream()
                    .filter(a -> a.getCreatedAt() != null &&
                            !a.getCreatedAt().isBefore(startOfDay) &&
                            a.getCreatedAt().isBefore(endOfDay) &&
                            a.getStatus() == Article.ArticleStatus.PUBLISHED)
                    .count();
            trendData.setArticleCount(articleCount);

            // 当天的评论数量（需要统计当天创建的评论）
            // 注意：这里使用所有文章的评论总和作为简化实现
            long commentCount = articleRepository.findAll().stream()
                    .filter(a -> a.getCreatedAt() != null &&
                            !a.getCreatedAt().isBefore(startOfDay) &&
                            a.getCreatedAt().isBefore(endOfDay))
                    .mapToLong(Article::getComments)
                    .sum();
            trendData.setCommentCount(commentCount);

            // 当天的访问量（当天创建/更新的文章的总浏览量）
            long viewCount = articleRepository.findAll().stream()
                    .filter(a -> (a.getCreatedAt() != null &&
                            !a.getCreatedAt().isBefore(startOfDay) &&
                            a.getCreatedAt().isBefore(endOfDay)) ||
                           (a.getUpdatedAt() != null &&
                            !a.getUpdatedAt().isBefore(startOfDay) &&
                            a.getUpdatedAt().isBefore(endOfDay)))
                    .mapToLong(Article::getViews)
                    .sum();
            trendData.setViewCount(viewCount);

            trendDataList.add(trendData);
        }

        chartData.setTrendData(trendDataList);

        // 2. 获取分类统计数据
        List<Article> allArticles = articleRepository.findAll();
        Map<Category, List<Article>> articlesByCategory = allArticles.stream()
                .filter(a -> a.getCategory() != null && a.getStatus() == Article.ArticleStatus.PUBLISHED)
                .collect(Collectors.groupingBy(Article::getCategory));

        List<DashboardChartData.CategoryStat> categoryStats = articlesByCategory.entrySet().stream()
                .map(entry -> {
                    DashboardChartData.CategoryStat stat = new DashboardChartData.CategoryStat();
                    stat.setName(entry.getKey().getName());
                    stat.setCount((long) entry.getValue().size());
                    return stat;
                })
                .collect(Collectors.toList());

        chartData.setCategoryStats(categoryStats);

        return chartData;
    }
}
