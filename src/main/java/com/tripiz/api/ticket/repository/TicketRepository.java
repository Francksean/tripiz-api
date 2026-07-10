package com.tripiz.api.ticket.repository;

import com.tripiz.api.domain.Ticket;
import com.tripiz.api.domain.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    long countByStatusAndUseDateBetween(TicketStatus status, LocalDateTime start, LocalDateTime end);

    long countByPurchaseDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.purchaseDate BETWEEN :start AND :end")
    Double sumPriceByPurchaseDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t.paymentMethod, COUNT(t) FROM Ticket t WHERE t.purchaseDate BETWEEN :start AND :end GROUP BY t.paymentMethod")
    List<Object[]> countByPaymentMethodBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t.tripId, COUNT(t) FROM Ticket t WHERE t.purchaseDate BETWEEN :start AND :end GROUP BY t.tripId")
    List<Object[]> countByTripIdBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Ticket> findByUserId(UUID userId);
}