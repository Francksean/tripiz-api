package com.tripiz.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ticket_id")
    private UUID ticketId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "use_date")
    private LocalDateTime useDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "price")
    private Double price;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
}