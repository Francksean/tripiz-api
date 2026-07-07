package com.tripiz.api.dashboard.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DashboardFilters {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String period; // "24h", "7d", "30d", "year"
}