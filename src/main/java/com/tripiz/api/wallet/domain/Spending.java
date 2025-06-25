package com.tripiz.api.wallet.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Spending extends Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID paymentId;

}
