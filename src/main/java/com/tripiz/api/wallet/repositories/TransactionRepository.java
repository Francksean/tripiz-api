package com.tripiz.api.wallet.repositories;

import com.tripiz.api.wallet.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWalletIdOrderByTimestampDesc(UUID wallet_id);
    Optional<Transaction> findByReference(String reference);

    // Nouvelle méthode pour compter par type et période
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionType = :type AND t.timestamp BETWEEN :start AND :end")
    long countByTransactionTypeAndTimestampBetween(
            @Param("type") String type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Nouvelle méthode pour sommer les montants par type et période
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.transactionType = :type AND t.timestamp BETWEEN :start AND :end")
    Double sumAmountByTransactionTypeAndTimestampBetween(
            @Param("type") String type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}