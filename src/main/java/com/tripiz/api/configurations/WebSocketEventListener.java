package com.tripiz.api.configurations;

import com.tripiz.api.domain.BusPosition;
import com.tripiz.api.domain.PositionMessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("A bus connected via WebSocket");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        UUID busId = (UUID) headerAccessor.getSessionAttributes().get("busId");

        if (busId != null) {
            log.info("Bus disconnected: {}", busId);

            BusPosition position = BusPosition.builder()
                    .busId(busId)
                    .type(PositionMessageType.LEAVE)
                    .build();

            messagingTemplate.convertAndSend("/topic/positions", position);
        }
    }
}

