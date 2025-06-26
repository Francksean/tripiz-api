package com.tripiz.api.wallet.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tripiz.api.wallet.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "transactions")
public abstract class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    private LocalDateTime timestamp;
    private String reference; // Identifiant unique métier

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    @JsonBackReference // Empêche la sérialisation circulaire côté enfant
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

}

