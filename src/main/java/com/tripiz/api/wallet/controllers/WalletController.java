package com.tripiz.api.wallet.controllers;

import com.tripiz.api.wallet.domain.Transaction;
import com.tripiz.api.wallet.domain.Wallet;
import com.tripiz.api.wallet.dto.RechargeRequestDTO;
import com.tripiz.api.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/me")
    public ResponseEntity<?> fetchUserWallet(
            @RequestParam(name = "userId", required = true) String userIdStr) {

        try {
            UUID userId = UUID.fromString(userIdStr);
            Wallet wallet = walletService.getUserWallet(userId);
            return ResponseEntity.ok(wallet);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    e.getMessage());

        }
    }

    @PostMapping("/recharge")
    public ResponseEntity<?> initiateRecharge(@RequestBody RechargeRequestDTO request) {
        try {
            Transaction transaction = walletService.initiateRecharge(
                    request.getWalletId(),
                    request.getAmount(),
                    request.getPaymentMethod()
            );
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/recharge/confirm/{transactionId}")
    public ResponseEntity<?> confirmRecharge(@PathVariable UUID transactionId, @RequestParam boolean success) {
        try {
            walletService.confirmRecharge(transactionId, success);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
