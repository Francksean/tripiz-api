package com.tripiz.api.ticket.dto;

import com.tripiz.api.domain.TicketStatus;
import com.tripiz.api.wallet.enums.TransactionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
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
    private TicketStatus ticketStatus;
    private LocalDateTime purchaseDate;
    private LocalDateTime useDate;
    private LocalDateTime expirationDate;
}