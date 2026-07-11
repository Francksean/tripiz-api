package com.tripiz.api.repository;

import com.tripiz.api.domain.CompanyConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyConfigRepository extends JpaRepository<CompanyConfig, UUID> {
}