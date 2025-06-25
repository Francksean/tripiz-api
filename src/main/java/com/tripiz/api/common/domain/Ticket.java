package com.tripiz.api.common.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID user_id;
    private String first_name;
    private String last_name;
    private String email;
    private String profile_image;
    private String phone;
    private String status;
    private String password;
    @Builder.Default
    private String role = "client";
}



