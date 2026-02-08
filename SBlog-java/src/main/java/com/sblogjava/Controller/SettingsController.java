package com.sblogjava.Controller;

import com.sblogjava.Dto.SystemSettingsDto;
import com.sblogjava.common.Result;
import com.sblogjava.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping
    public Result<SystemSettingsDto> getSettings() {
        return Result.success(settingsService.getSettings());
    }

    @PutMapping
    public Result<Void> updateSettings(@RequestBody SystemSettingsDto settings) {
        settingsService.updateSettings(settings);
        return Result.success("更新成功", null);
    }
}
