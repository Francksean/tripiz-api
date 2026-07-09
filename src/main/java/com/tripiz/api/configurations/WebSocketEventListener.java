package com.tripiz.api.configurations;

import com.tripiz.api.domain.BusPosition;
import com.tripiz.api.domain.PositionMessageType;
import com.tripiz.api.service.BusPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final BusPositionService busPositionService;
    private final StationSubscriptionTracker stationSubscriptionTracker;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("A bus connected via WebSocket");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor =
                StompHeaderAccessor.wrap(event.getMessage());

        if (headerAccessor.getSessionAttributes() == null) {
            return;
        }

        UUID busId = (UUID) headerAccessor.getSessionAttributes().get("busId");

        if (busId != null) {

            log.info("Bus disconnected: {}", busId);

            busPositionService.removePosition(busId);

            BusPosition position = BusPosition.builder()
                    .busId(busId)
                    .type(PositionMessageType.LEAVE)
                    .build();

            messagingTemplate.convertAndSend("/topic/positions", position);
        }
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        if (destination != null) {
            stationSubscriptionTracker.track(accessor.getSessionId(), accessor.getSubscriptionId(), destination);
        }
    }


    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        stationSubscriptionTracker.untrack(accessor.getSessionId(), accessor.getSubscriptionId());
    }
}