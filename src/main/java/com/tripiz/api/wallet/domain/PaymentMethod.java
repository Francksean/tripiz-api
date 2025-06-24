package com.tripiz.api.wallet.domain;

import com.tripiz.api.wallet.enums.PaymentMethodType;
import com.tripiz.api.common.domain.User;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_methods")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private PaymentMethodType type;

    private String tokenizedData; // Données chiffrées (carte bancaire, etc.)
    private boolean isDefault;
}

