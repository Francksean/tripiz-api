package com.tripiz.api.dashboard.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDate;

@Data
public class DashboardFilters {
    private LocalDate startDate;
    private LocalDate endDate;
    private String period;
}