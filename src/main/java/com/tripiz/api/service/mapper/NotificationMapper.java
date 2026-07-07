package com.tripiz.api.service.mapper;

import com.tripiz.api.domain.Notification;
import com.tripiz.api.model.NotificationDTO;

public class NotificationMapper {

    private NotificationMapper() {}

    public static NotificationDTO toDto(Notification notification) {
        return new NotificationDTO(
                notification.getNotificationId(),
                notification.getTitle(),
                notification.getBody(),
                notification.getCreatedAt(),
                notification.getIsRead()
        );
    }
}
