package com.sblogjava.service;

import com.sblogjava.Dto.SystemSettingsDto;

public interface SettingsService {
    SystemSettingsDto getSettings();

    void updateSettings(SystemSettingsDto settings);
}
