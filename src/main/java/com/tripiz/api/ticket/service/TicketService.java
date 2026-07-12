package com.tripiz.api.ticket.service;

import com.tripiz.api.domain.*;
import com.tripiz.api.repository.ItineraryRepository;
import com.tripiz.api.repository.TripRepository;
import com.tripiz.api.service.CompanyConfigService;
import com.tripiz.api.ticket.dto.TicketDTO;
import com.tripiz.api.ticket.dto.TicketHistoryDTO;
import com.tripiz.api.ticket.repository.TicketRepository;
import com.tripiz.api.wallet.domain.Spending;
import com.tripiz.api.wallet.domain.Wallet;
import com.tripiz.api.wallet.enums.TransactionStatus;
import com.tripiz.api.wallet.repositories.TransactionRepository;
import com.tripiz.api.wallet.repositories.WalletRepository;
import com.tripiz.api.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;
    private final ItineraryRepository itineraryRepository;
    private final CompanyConfigService companyConfigService;
    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

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

    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID tripId, String paymentMethod) {
        // Récupérer le trajet
        Trip trip = tripRepository.n(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Récupérer l'itinéraire
        Itinerary itinerary = itineraryRepository.findById(trip.getItineraryId())
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));

        // Déterminer le prix
        Double price = itinerary.getTicketPrice();
        if (price == null) {
            CompanyConfig config = companyConfigService.getConfig();
            price = config.getDefaultTicketPrice();
        }

        // Vérifier le solde du wallet
        Wallet wallet = walletService.getUserWallet(userId);
        if (wallet.getBalance() < price) {
            throw new RuntimeException("Insufficient funds");
        }

        // Débiter le wallet
        wallet.setBalance(wallet.getBalance() - price);
        walletRepository.save(wallet);

        // Créer le ticket
        Ticket ticket = Ticket.builder()
                .userId(userId)
                .tripId(tripId)
                .purchaseDate(LocalDateTime.now())
                .expirationDate(trip.getTripDate().atTime(23, 59, 59))
                .price(price)
                .paymentMethod(paymentMethod)
                .status(TicketStatus.VALID)
                .build();

        Ticket saved = ticketRepository.save(ticket);

        // Enregistrer la transaction de paiement (débit)
        Spending spending = new Spending();
        spending.setWallet(wallet);
        spending.setAmount(price);
        spending.setTimestamp(LocalDateTime.now());
        spending.setReference("TICKET-" + saved.getTicketId().toString());
        spending.setStatus(TransactionStatus.COMPLETE);
        transactionRepository.save(spending);

        return saved;
    }
}