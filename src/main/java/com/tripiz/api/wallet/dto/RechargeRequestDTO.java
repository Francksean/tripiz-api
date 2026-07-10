package com.tripiz.api.wallet.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class RechargeRequestDTO {
    private UUID walletId;
    private double amount;
    private String paymentMethod; // ex: "ORANGE_MONEY", "MTN_MOMO"
}