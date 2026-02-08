package com.sblogjava.Dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardDto {
    private Long articleCount;
    private Long draftCount;
    private Long commentCount;
    private Long messageCount;
    private Long userCount;
    private Long viewCount;
    private Long todayViews;
    private List<RecentArticle> recentArticles;

    @Data
    public static class RecentArticle {
        private Long id;
        private String title;
        private Integer views;
        private String date;
    }
}
