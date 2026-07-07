package com.tripiz.api.ticket.service;

import com.tripiz.api.domain.Ticket;
import com.tripiz.api.domain.Trip;
import com.tripiz.api.repository.TripRepository;
import com.tripiz.api.ticket.dto.TicketDTO;
import com.tripiz.api.ticket.dto.TicketHistoryDTO;
import com.tripiz.api.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private TicketDTO toDTO(Ticket ticket) {
        return TicketDTO.builder()
                .ticketId(ticket.getTicketId())
                .userId(ticket.getUserId())
                .tripId(ticket.getTripId())
                .purchaseDate(ticket.getPurchaseDate())
                .useDate(ticket.getUseDate())
                .expirationDate(ticket.getExpirationDate())
                .price(ticket.getPrice())
                .paymentMethod(ticket.getPaymentMethod())
                .status(ticket.getStatus())
                .build();
    }

    public List<TicketHistoryDTO> getTicketHistoryForUser(UUID userId) {
        List<Ticket> tickets = ticketRepository.findByUserId(userId);
        return tickets.stream()
                .map(ticket -> {
                    Trip trip = tripRepository.findById(ticket.getTripId()).orElse(null);
                    return TicketHistoryDTO.builder()
                            .ticketId(ticket.getTicketId())
                            .tripId(ticket.getTripId())
                            .tripName(trip != null ? "Trip " + trip.getTripId().toString().substring(0, 8) : null)
                            .tripDate(trip != null ? trip.getTripDate() : null)
                            .scheduleDeparture(trip != null ? trip.getScheduleDeparture() : null)
                            .actualDeparture(trip != null ? trip.getActualDeparture() : null)
                            .tripStatus(trip != null ? trip.getTripStatus().name() : null)
                            .price(ticket.getPrice())
                            .paymentMethod(ticket.getPaymentMethod())
                            .ticketStatus(ticket.getStatus())
                            .purchaseDate(ticket.getPurchaseDate())
                            .useDate(ticket.getUseDate())
                            .expirationDate(ticket.getExpirationDate())
                            .build();
                })
                .collect(Collectors.toList());
    }
}