package com.tripiz.api.model;

import lombok.Data;
import java.util.UUID;

@Data
public class PaymentRequestDTO {
    private UUID walletId;
    private Double amount;
    private String phone;
    private UUID tripId;
}