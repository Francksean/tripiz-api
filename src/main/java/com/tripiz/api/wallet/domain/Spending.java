package com.tripiz.api.wallet.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SPENDING")
public class Spending extends Transaction {
}