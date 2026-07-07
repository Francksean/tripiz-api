package com.tripiz.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDTO(
        UUID notificationId,
        String title,
        String body,
        LocalDateTime createdAt,
        Boolean isRead
) {}