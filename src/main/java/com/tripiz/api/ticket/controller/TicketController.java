package com.tripiz.api.ticket.controller;

import com.tripiz.api.domain.Ticket;
import com.tripiz.api.domain.User;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.ticket.dto.PurchaseRequestDTO;
import com.tripiz.api.ticket.dto.TicketDTO;
import com.tripiz.api.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final UserRepository userRepository;

    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('admin')")
    public List<TicketDTO> listTickets() {
        return ticketService.getAllTickets();
    }

    @PostMapping("/purchase")
    public ResponseEntity<Ticket> purchaseTicket(@RequestBody PurchaseRequestDTO request, @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = ticketService.purchaseTicket(user.getUserId(), request.getTripId(), "WALLET");
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}