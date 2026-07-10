package com.tripiz.api.wallet.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionDTO {
    private UUID transactionId;
    private double amount;
    private String type;
    private String status;
    private LocalDateTime createdAt;
    private String reference;
}