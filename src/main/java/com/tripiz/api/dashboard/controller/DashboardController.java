package com.tripiz.api.dashboard.controller;

import com.tripiz.api.dashboard.dto.DashboardStatsDTO;
import com.tripiz.api.dashboard.dto.DashboardFilters;
import com.tripiz.api.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardStatsDTO getStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String period) {

        DashboardFilters filters = new DashboardFilters();
        if (startDate != null && endDate != null) {
            filters.setStartDate(startDate);
            filters.setEndDate(endDate);
        } else if (period != null) {
            // Gérer les périodes : 24h, 7d, 30d, year
            LocalDateTime now = LocalDateTime.now();
            switch (period) {
                case "24h":
                    filters.setStartDate(now.minusHours(24));
                    filters.setEndDate(now);
                    break;
                case "7d":
                    filters.setStartDate(now.minusDays(7));
                    filters.setEndDate(now);
                    break;
                case "30d":
                    filters.setStartDate(now.minusDays(30));
                    filters.setEndDate(now);
                    break;
                case "year":
                    filters.setStartDate(now.minusYears(1));
                    filters.setEndDate(now);
                    break;
                default:
                    // Par défaut aujourd'hui
                    filters.setStartDate(now.withHour(0).withMinute(0).withSecond(0));
                    filters.setEndDate(now);
            }
        } else {
            // Par défaut aujourd'hui
            LocalDateTime now = LocalDateTime.now();
            filters.setStartDate(now.withHour(0).withMinute(0).withSecond(0));
            filters.setEndDate(now);
        }

        return dashboardService.getDashboardStats(filters);
    }
}