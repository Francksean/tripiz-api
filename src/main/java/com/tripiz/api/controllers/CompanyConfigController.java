package com.tripiz.api.controllers;

import com.tripiz.api.domain.CompanyConfig;
import com.tripiz.api.service.CompanyConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/config")
@RequiredArgsConstructor
public class CompanyConfigController {

    private final CompanyConfigService companyConfigService;

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CompanyConfig> getConfig() {
        return ResponseEntity.ok(companyConfigService.getConfig());
    }

    @PutMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CompanyConfig> updateConfig(@RequestBody CompanyConfig config) {
        return ResponseEntity.ok(companyConfigService.updateConfig(config));
    }
}