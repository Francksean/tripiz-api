package com.tripiz.api.wallet.service;

import com.tripiz.api.domain.User;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.wallet.domain.Recharge;
import com.tripiz.api.wallet.domain.Spending;
import com.tripiz.api.wallet.domain.Transaction;
import com.tripiz.api.wallet.domain.Wallet;
import com.tripiz.api.wallet.dto.TransactionDTO;
import com.tripiz.api.wallet.enums.TransactionStatus;
import com.tripiz.api.wallet.repositories.TransactionRepository;
import com.tripiz.api.wallet.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository; // 👈 Injecté

    public Wallet createWalletForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setWalletReference(UUID.randomUUID().toString());
        wallet.setBalance(0.0);

        return walletRepository.save(wallet);
    }

    public Wallet getUserWallet(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> createWalletForUser(userId));
    }

    public double getWalletBalance(UUID walletId) {
        return walletRepository.findById(walletId)
                .map(Wallet::getBalance)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId));
    }

    public Wallet getWalletById(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId));
    }

    public boolean walletExists(UUID walletId) {
        return walletRepository.existsById(walletId);
    }

    public void updateWalletBalance(UUID walletId, double newBalance) {
        Wallet wallet = getWalletById(walletId);
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
    }

    // ---- Recharge ----

    public Recharge initiateRecharge(UUID walletId, double amount, String paymentMethod) {
        Wallet wallet = getWalletById(walletId);

        Recharge recharge = new Recharge();
        recharge.setWallet(wallet);
        recharge.setAmount(amount);
        recharge.setTimestamp(LocalDateTime.now());
        recharge.setReference("RECH-" + UUID.randomUUID().toString());
        recharge.setStatus(TransactionStatus.PENDING);
        recharge = (Recharge) transactionRepository.save(recharge);

        // 🔥 Ici, vous appellerez l'API de paiement externe (Orange Money, etc.)
        // Pour l'instant, on simule un succès immédiat :
        // this.confirmRecharge(recharge.getId(), true);

        return recharge;
    }

    public void confirmRecharge(UUID transactionId, boolean success) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction already processed");
        }

        if (success) {
            transaction.setStatus(TransactionStatus.COMPLETE);
            Wallet wallet = getWalletById(transaction.getWallet().getId());
            wallet.setBalance(wallet.getBalance() + transaction.getAmount());
            walletRepository.save(wallet);
        } else {
            transaction.setStatus(TransactionStatus.FAILED);
        }
        transactionRepository.save(transaction);
    }

    // ---- Historique ----

    public List<TransactionDTO> getTransactions(UUID walletId) {
        Wallet wallet = getWalletById(walletId);
        List<Transaction> transactions = wallet.getTransactions();

        return transactions.stream()
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .map(t -> TransactionDTO.builder()
                        .transactionId(t.getId())
                        .amount(t.getAmount())
                        .type(t.getTransactionType())
                        .status(t.getStatus().name())
//                        .timestamp(t.getTimestamp())
                        .reference(t.getReference())
                        .build())
                .collect(Collectors.toList());
    }
}