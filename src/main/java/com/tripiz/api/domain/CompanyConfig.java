package com.tripiz.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "company_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String companyName;

    private String logoUrl;

    @Column(nullable = false)
    private Double defaultTicketPrice;
}