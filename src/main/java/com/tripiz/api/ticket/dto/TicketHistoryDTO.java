package com.tripiz.api.ticket.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class TicketHistoryDTO {
    private UUID ticketId;
    private UUID tripId;
    private String tripName;
    private LocalDate tripDate;
    private LocalTime scheduleDeparture;
    private LocalTime actualDeparture;
    private String tripStatus;
    private Double price;
    private String paymentMethod;
    private String ticketStatus;
    private LocalDate purchaseDate;
    private LocalDate useDate;
    private LocalDate expirationDate;
}