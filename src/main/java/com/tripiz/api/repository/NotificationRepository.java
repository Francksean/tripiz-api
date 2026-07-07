package com.tripiz.api.repository;

import com.tripiz.api.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(UUID userId);

    long countByUser_UserIdAndIsReadFalse(UUID userId);
}
