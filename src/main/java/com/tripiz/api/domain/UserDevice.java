package com.tripiz.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_device")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID deviceId;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(length = 500)
    private String fcmToken;

    private String platform;

    private boolean active;

    private LocalDate lastConnection;

}
