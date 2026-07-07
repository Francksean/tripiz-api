package com.tripiz.api.ticket.controller;

import com.tripiz.api.ticket.dto.TicketDTO;
import com.tripiz.api.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/admin/list")
    public List<TicketDTO> listTickets() {
        return ticketService.getAllTickets();
    }
}