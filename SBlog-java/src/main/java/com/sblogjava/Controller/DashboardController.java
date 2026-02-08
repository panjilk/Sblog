package com.sblogjava.Controller;

import com.sblogjava.Dto.DashboardDto;
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
}
