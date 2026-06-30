package com.tripiz.api.model;

import lombok.Data;
import java.util.UUID;

@Data
public class RechargeRequestDTO {
    private UUID walletId;
    private Double amount;
    private String phone;
    private String channel;
}