package com.tripiz.api.payment.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class InvoiceDTO {
    private UUID ticketId;
    private UUID tripId;
    private double amount;
    private LocalDateTime purchaseDate;
    private String paymentMethod;
    private String passengerName;
    private String tripName;
    private String fromStation;
    private String toStation;
}