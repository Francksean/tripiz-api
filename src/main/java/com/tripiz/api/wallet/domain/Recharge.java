package com.tripiz.api.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("RECHARGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recharge extends Transaction {

    @Column(name = "recharger_number")
    private String rechargerNumber;

    @Column(name = "channel")
    private String channel;

    @Column(name = "payment_gateway_reference")
    private String paymentGatewayReference;
}