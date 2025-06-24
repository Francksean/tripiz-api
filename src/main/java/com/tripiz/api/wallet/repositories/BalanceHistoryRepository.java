package com.tripiz.api.wallet.repositories;

import com.tripiz.api.wallet.domain.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, Long> {
    List<BalanceHistory> findByWalletIdOrderByChangeDateDesc(UUID wallet_id);
}
