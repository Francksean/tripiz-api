package com.tripiz.api.wallet.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("RECHARGE")
public class Recharge extends Transaction {
    private String paymentGatewayReference;
    private String rechargerNumber;
    private String channel;

}