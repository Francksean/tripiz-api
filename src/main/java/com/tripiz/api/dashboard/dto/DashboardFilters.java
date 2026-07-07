package com.tripiz.api.dashboard.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalDateTime;

@Data
public class DashboardFilters {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String period;
}