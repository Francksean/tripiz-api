package com.tripiz.api.payment.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class PaymentDataDTO {
    private UUID ticketId;
    private UUID tripId;
    private UUID walletId;
    private double amount;
    private long timestamp;
    private String signature;
}