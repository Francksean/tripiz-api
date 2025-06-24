package com.tripiz.api.wallet.repositories;

import aj.org.objectweb.asm.commons.Remapper;
import com.tripiz.api.wallet.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByWalletReference(String reference);
    Optional<Wallet> findByUserId(Long userId);

    Optional<Wallet> findById(UUID walletId);
}
