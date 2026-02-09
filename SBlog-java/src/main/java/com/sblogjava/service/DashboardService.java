package com.sblogjava.service;

import com.sblogjava.Dto.DashboardDto;
import com.sblogjava.Dto.DashboardChartData;

public interface DashboardService {
    DashboardDto getDashboard();

    /**
     * 获取仪表盘图表数据
     * @param days 天数
     * @return 图表数据
     */
    DashboardChartData getChartData(int days);
}
