package com.tripiz.api.wallet.service;

import com.tripiz.api.domain.User;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.wallet.domain.Wallet;
import com.tripiz.api.wallet.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

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
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for user: " + userId));
    }

    // Recherche par objet User
    public Wallet getUserWalletByUser(User user) {
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for user: " + user.getUserId()));
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

    // Méthodes utilitaires supplémentaires
    public boolean walletExists(UUID walletId) {
        return walletRepository.existsById(walletId);
    }

    public void updateWalletBalance(UUID walletId, double newBalance) {
        Wallet wallet = getWalletById(walletId);
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
    }
}