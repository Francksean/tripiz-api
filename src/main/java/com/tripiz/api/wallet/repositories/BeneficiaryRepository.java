package com.tripiz.api.wallet.repositories;

import com.tripiz.api.wallet.domain.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByUserId(Long userId);
}