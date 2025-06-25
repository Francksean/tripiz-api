package com.tripiz.api.wallet.service;

import com.tripiz.api.common.domain.User;
import com.tripiz.api.common.repository.UserRepository;
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
    WalletRepository walletRepository;
    @Autowired
    UserRepository userRepository;

    public Wallet createWalletForUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setWalletReference(UUID.randomUUID().toString());
        wallet.setBalance(0.0);

        // Initialiser les limites par défaut
//        initDefaultLimits(wallet);

        return walletRepository.save(wallet);
    }

//    private void initDefaultLimits(Wallet wallet) {
//        Arrays.stream(LimitType.values()).forEach(type -> {
//            TransactionLimit limit = new TransactionLimit();
//            limit.setWallet(wallet);
//            limit.setLimitType(type);
//            limit.setDailyLimit(type == LimitType.PAYMENT ?
//                    new BigDecimal("1000") : new BigDecimal("5000"));
//            limit.setPerTransactionLimit(new BigDecimal("500"));
//            limitRepository.save(limit);
//        });
//    }


    public Wallet getUserWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));
    }

    public double getWalletBalance(UUID walletId) {
        return walletRepository.findById(walletId)
                .map(Wallet::getBalance)
                .orElseThrow();
    }

}
