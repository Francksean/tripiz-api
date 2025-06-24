package com.tripiz.api.wallet.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
public class Payment extends Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID payment_id;

    private String merchantCode;
    private String orderReference;

    @ManyToOne
    @JoinColumn(name = "beneficiary_id")
    private Beneficiary beneficiary;
}
