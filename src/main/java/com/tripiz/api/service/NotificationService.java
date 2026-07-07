package com.tripiz.api.service;

import com.tripiz.api.domain.Notification;
import com.tripiz.api.domain.User;
import com.tripiz.api.model.NotificationDTO;
import com.tripiz.api.service.mapper.NotificationMapper;
import com.tripiz.api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;


    /**
     * Persiste la notification puis la pousse en temps réel à l'utilisateur ciblé.
     * Le push ciblé repose sur convertAndSendToUser, qui exige que le Principal
     * de la session WebSocket de cet utilisateur porte bien son userId (voir
     * WebSocketAuthInterceptor).
     */
    public NotificationDTO createAndSend(User user, String title, String body) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .body(body)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationDTO dto = NotificationMapper.toDto(saved);

        messagingTemplate.convertAndSendToUser(
                user.getUserId().toString(),
                "/queue/notifications",
                dto
        );

        return dto;
    }

    public List<NotificationDTO> getNotificationsForUser(UUID userId) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationMapper::toDto)
                .toList();
    }

    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification introuvable : " + notificationId));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public long countUnread(UUID userId) {
        return notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
    }
}
