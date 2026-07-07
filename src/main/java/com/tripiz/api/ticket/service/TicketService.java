package com.tripiz.api.ticket.service;

import com.tripiz.api.domain.Ticket;
import com.tripiz.api.ticket.dto.TicketDTO;
import com.tripiz.api.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

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
}