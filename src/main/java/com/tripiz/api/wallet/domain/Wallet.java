package com.tripiz.api.wallet.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tripiz.api.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wallet_id")
    private UUID id;

    @Column(unique = true)
    private String walletReference;

    private double balance = 0.0;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference // Empêche la sérialisation circulaire côté parent
    private User user;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Permet la sérialisation côté enfant
    private List<Transaction> transactions = new ArrayList<>();
}