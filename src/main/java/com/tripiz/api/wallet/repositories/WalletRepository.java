package com.tripiz.api.wallet.repositories;

import com.tripiz.api.domain.User;
import com.tripiz.api.wallet.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> { // UUID car l'ID de Wallet est UUID

    Optional<Wallet> findByWalletReference(String reference);

    // Option 1: Recherche par objet User complet
    Optional<Wallet> findByUser(User user);

    // Recherche par ID utilisateur - ATTENTION: User.id s'appelle "userId"
    @Query("SELECT w FROM Wallet w WHERE w.user.userId = :userId")
    Optional<Wallet> findByUserId(@Param("userId") UUID userId);
}