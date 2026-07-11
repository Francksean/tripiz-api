package com.tripiz.api.service;

import com.tripiz.api.domain.CompanyConfig;
import com.tripiz.api.repository.CompanyConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyConfigService {

    private final CompanyConfigRepository companyConfigRepository;

    public CompanyConfig getConfig() {
        return companyConfigRepository.findAll().stream().findFirst()
                .orElseGet(this::createDefaultConfig);
    }

    public CompanyConfig updateConfig(CompanyConfig config) {
        return companyConfigRepository.findAll().stream().findFirst()
                .map(existing -> {
                    existing.setCompanyName(config.getCompanyName());
                    existing.setLogoUrl(config.getLogoUrl());
                    existing.setDefaultTicketPrice(config.getDefaultTicketPrice());
                    return companyConfigRepository.save(existing);
                })
                .orElseGet(() -> companyConfigRepository.save(config));
    }

    private CompanyConfig createDefaultConfig() {
        CompanyConfig defaultConfig = CompanyConfig.builder()
                .companyName("Tripiz")
                .logoUrl("")
                .defaultTicketPrice(1500.0)
                .build();
        return companyConfigRepository.save(defaultConfig);
    }
}