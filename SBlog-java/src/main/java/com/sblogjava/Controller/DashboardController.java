package com.sblogjava.Controller;

import com.sblogjava.Dto.DashboardDto;
import com.sblogjava.Dto.DashboardChartData;
import com.sblogjava.common.Result;
import com.sblogjava.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public Result<DashboardDto> getDashboard() {
        return Result.success(dashboardService.getDashboard());
    }

    /**
     * 获取仪表盘图表数据
     * @param days 天数，默认7天
     * @return 图表数据
     */
    @GetMapping("/dashboard/chart")
    public Result<DashboardChartData> getChartData(
            @RequestParam(defaultValue = "7") int days) {
        return Result.success(dashboardService.getChartData(days));
    }
}
