package com.tripiz.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID notificationId;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private String title;

    @Column(length = 1000)
    private String body;

    private LocalDateTime createdAt;

    private Boolean isRead;

}
