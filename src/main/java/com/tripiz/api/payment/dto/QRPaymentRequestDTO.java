package com.tripiz.api.payment.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class QRPaymentRequestDTO {
    private UUID ticketId;
    private UUID tripId;
    private UUID walletId;
    private double amount;
    private String signature;
    private long timestamp;
}