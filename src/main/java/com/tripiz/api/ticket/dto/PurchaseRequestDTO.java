package com.tripiz.api.ticket.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PurchaseRequestDTO {
    private UUID tripId;
}