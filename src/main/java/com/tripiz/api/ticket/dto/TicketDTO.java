package com.tripiz.api.ticket.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class TicketDTO {
    private UUID ticketId;
    private UUID userId;
    private UUID tripId;
    private LocalDate purchaseDate;
    private LocalDate useDate;
    private LocalDate expirationDate;
    private Double price;
    private String paymentMethod;
    private String status;
}