package com.tripiz.api.wallet.controllers;

import com.tripiz.api.model.PaymentRequestDTO;
import com.tripiz.api.model.RechargeRequestDTO;
import com.tripiz.api.wallet.domain.Recharge;
import com.tripiz.api.wallet.domain.Spending;
import com.tripiz.api.wallet.exceptions.InsufficientFundsException;
import com.tripiz.api.wallet.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/recharge")
    public ResponseEntity<?> initiateRecharge(@RequestBody RechargeRequestDTO request) {
        try {
            Recharge recharge = transactionService.initiateRecharge(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(recharge);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Erreur lors de la recharge",
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/spending")
    public ResponseEntity<?> initiatePayment(@RequestBody PaymentRequestDTO request) {
        try {
            Spending payment = transactionService.initiatePayment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Fonds insuffisants",
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Erreur lors du paiement",
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/{transactionId}/complete")
    public ResponseEntity<?> completeTransaction(
            @PathVariable Long transactionId,
            @RequestParam boolean success) {

        try {
            transactionService.completeTransaction(transactionId, success);
            return ResponseEntity.ok().body(Map.of("message", "Transaction mise à jour avec succès"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Transaction invalide",
                            "message", e.getMessage()
                    ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Transaction non trouvée",
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Erreur lors de la mise à jour",
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/payment/{paymentId}/complete")
    public ResponseEntity<?> completePayment(@PathVariable Long paymentId) {
        try {
            transactionService.completePayment(paymentId);
            return ResponseEntity.ok().body(Map.of("message", "Paiement complété avec succès"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Paiement invalide",
                            "message", e.getMessage()
                    ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Paiement non trouvé",
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Erreur lors du traitement du paiement",
                            "message", e.getMessage()
                    ));
        }
    }
}
