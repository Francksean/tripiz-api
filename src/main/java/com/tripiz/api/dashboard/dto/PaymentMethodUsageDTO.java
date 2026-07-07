package com.tripiz.api.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentMethodUsageDTO {
    private String paymentMethod;
    private long count;
    private double percentage;
}
