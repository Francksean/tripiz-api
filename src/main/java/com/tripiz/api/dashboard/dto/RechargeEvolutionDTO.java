package com.tripiz.api.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RechargeEvolutionDTO {
    private String day;
    private long rechargeCount;
}
