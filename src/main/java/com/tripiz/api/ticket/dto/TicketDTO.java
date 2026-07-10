package com.tripiz.api.ticket.dto;

import com.tripiz.api.domain.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TicketDTO {
    private UUID ticketId;
    private UUID userId;
    private UUID tripId;
    private LocalDateTime purchaseDate;
    private LocalDateTime useDate;
    private LocalDateTime expirationDate;
    private Double price;
    private String paymentMethod;
    private TicketStatus status;
}