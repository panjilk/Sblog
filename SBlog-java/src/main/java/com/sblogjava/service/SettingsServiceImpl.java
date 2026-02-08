package com.sblogjava.service;

import com.sblogjava.Dto.SystemSettingsDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsServiceImpl implements SettingsService {

    // 使用内存存储，实际项目中应该使用数据库
    private SystemSettingsDto settings = new SystemSettingsDto();

    public SettingsServiceImpl() {
        // 初始化默认设置
        settings.setSiteName("Sblog");
        settings.setSiteUrl("https://example.com");
        settings.setDescription("一个简洁的博客系统");
        settings.setKeywords("博客,技术,分享");
        settings.setFooter("© 2024 Sblog");
    }

    @Override
    public SystemSettingsDto getSettings() {
        return settings;
    }

    @Override
    public void updateSettings(SystemSettingsDto newSettings) {
        if (newSettings.getSiteName() != null) {
            settings.setSiteName(newSettings.getSiteName());
        }
        if (newSettings.getSiteUrl() != null) {
            settings.setSiteUrl(newSettings.getSiteUrl());
        }
        if (newSettings.getLogo() != null) {
            settings.setLogo(newSettings.getLogo());
        }
        if (newSettings.getFavicon() != null) {
            settings.setFavicon(newSettings.getFavicon());
        }
        if (newSettings.getDescription() != null) {
            settings.setDescription(newSettings.getDescription());
        }
        if (newSettings.getKeywords() != null) {
            settings.setKeywords(newSettings.getKeywords());
        }
        if (newSettings.getFooter() != null) {
            settings.setFooter(newSettings.getFooter());
        }
    }
}
