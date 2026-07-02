package com.tripiz.api.repository;

import com.tripiz.api.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserUserIdOrderByCreatedAtDesc(UUID userId);

}
