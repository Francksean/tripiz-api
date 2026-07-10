package com.tripiz.api.payment.service;

import com.tripiz.api.domain.Ticket;
import com.tripiz.api.domain.TicketStatus;
import com.tripiz.api.payment.dto.InvoiceDTO;
import com.tripiz.api.payment.dto.PaymentDataDTO;
import com.tripiz.api.payment.dto.QRPaymentRequestDTO;
import com.tripiz.api.ticket.repository.TicketRepository;
import com.tripiz.api.wallet.domain.Spending;
import com.tripiz.api.wallet.domain.Wallet;
import com.tripiz.api.wallet.enums.TransactionStatus;
import com.tripiz.api.wallet.repositories.TransactionRepository;
import com.tripiz.api.wallet.repositories.WalletRepository;
import com.tripiz.api.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TicketRepository ticketRepository;

    @Value("${app.qr-secret-key:defaultSecretKey}")
    private String qrSecretKey;

    private static final long QR_VALIDITY_SECONDS = 300; // 5 minutes

    /**
     * Génère les données pour le QR code.
     */
    public PaymentDataDTO generateQRData(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        Wallet wallet = walletService.getUserWallet(ticket.getUserId());

        long timestamp = Instant.now().getEpochSecond();

        PaymentDataDTO data = PaymentDataDTO.builder()
                .ticketId(ticketId)
                .tripId(ticket.getTripId())
                .walletId(wallet.getId())
                .amount(ticket.getPrice())
                .timestamp(timestamp)
                .build();

        String signature = generateSignature(data);
        data.setSignature(signature);

        return data;
    }

    /**
     * Traite le paiement après scan du QR code.
     */
    @Transactional
    public void processQRPayment(QRPaymentRequestDTO request) {
        // 1. Vérifier la signature
        if (!verifySignature(request)) {
            throw new RuntimeException("Invalid QR code signature");
        }

        // 2. Vérifier la validité temporelle
        long now = Instant.now().getEpochSecond();
        if (now - request.getTimestamp() > QR_VALIDITY_SECONDS) {
            throw new RuntimeException("QR code expired");
        }

        // 3. Vérifier le ticket
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() == TicketStatus.USED || ticket.getStatus() == TicketStatus.EXPIRED) {
            throw new RuntimeException("Ticket already used or expired");
        }

        // 4. Vérifier le wallet et le solde
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient funds");
        }

        // 5. Débiter le wallet
        double newBalance = wallet.getBalance() - request.getAmount();
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        // 6. Créer la transaction de type Spending
        Spending spending = new Spending();
        spending.setWallet(wallet);
        spending.setAmount(request.getAmount());
        spending.setTimestamp(LocalDateTime.now());
        spending.setReference("QR-" + UUID.randomUUID().toString());
        spending.setStatus(TransactionStatus.COMPLETE);
        transactionRepository.save(spending);

        // 7. Mettre à jour le ticket
        ticket.setStatus(TicketStatus.USED); // ou VALID selon votre logique
        ticket.setPaymentMethod("QR_CODE");
        ticket.setPurchaseDate(LocalDateTime.now());
        ticketRepository.save(ticket);

        log.info("Paiement QR effectué pour le ticket {} montant {}", ticket.getTicketId(), request.getAmount());
    }

    /**
     * Génère une facture pour un ticket.
     */
    public InvoiceDTO generateInvoice(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Récupérer les infos du trajet et des stations (simplifié)
        // Vous devrez injecter TripRepository et ItineraryRepository
        // Pour l'instant, on met des valeurs fictives

        return InvoiceDTO.builder()
                .ticketId(ticket.getTicketId())
                .tripId(ticket.getTripId())
                .amount(ticket.getPrice())
                .purchaseDate(ticket.getPurchaseDate())
                .paymentMethod(ticket.getPaymentMethod())
                .passengerName("Passager") // à remplacer par le nom de l'utilisateur
                .tripName("Trip " + ticket.getTripId().toString().substring(0, 8))
                .fromStation("Départ") // à remplacer par le nom de la station
                .toStation("Arrivée")
                .build();
    }

    // --- Méthodes de signature (HMAC) ---

    private String generateSignature(PaymentDataDTO data) {
        try {
            String payload = data.getTicketId().toString() +
                    data.getTripId().toString() +
                    data.getWalletId().toString() +
                    data.getAmount() +
                    data.getTimestamp();

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(qrSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] signatureBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }

    private boolean verifySignature(QRPaymentRequestDTO request) {
        try {
            String payload = request.getTicketId().toString() +
                    request.getTripId().toString() +
                    request.getWalletId().toString() +
                    request.getAmount() +
                    request.getTimestamp();

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(qrSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] calculatedSignature = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String calculatedBase64 = Base64.getEncoder().encodeToString(calculatedSignature);

            return calculatedBase64.equals(request.getSignature());
        } catch (Exception e) {
            log.error("Signature verification failed", e);
            return false;
        }
    }
}