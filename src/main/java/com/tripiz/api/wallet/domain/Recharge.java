package com.tripiz.api.wallet.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recharge")
public class Recharge extends Transaction {
    private String paymentGatewayReference; // ID de la transaction chez le processeur de paiement

}