package com.tripiz.api.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RevenueEvolutionDTO {
    private String day;
    private double revenue;
}
