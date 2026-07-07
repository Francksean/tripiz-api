package com.tripiz.api.ticket.repository;

import com.tripiz.api.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    long countByStatusAndUseDateBetween(String status, LocalDate start, LocalDate end);

    long countByPurchaseDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.purchaseDate BETWEEN :start AND :end")
    Double sumPriceByPurchaseDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT t.paymentMethod, COUNT(t) FROM Ticket t WHERE t.purchaseDate BETWEEN :start AND :end GROUP BY t.paymentMethod")
    List<Object[]> countByPaymentMethodBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT t.tripId, COUNT(t) FROM Ticket t WHERE t.purchaseDate BETWEEN :start AND :end GROUP BY t.tripId")
    List<Object[]> countByTripIdBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    List<Ticket> findByUserId(UUID userId);
}