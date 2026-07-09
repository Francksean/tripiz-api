package com.tripiz.api.controllers;

import com.tripiz.api.domain.BusPosition;
import com.tripiz.api.domain.PositionMessageType;
import com.tripiz.api.domain.Trip;
import com.tripiz.api.domain.TripStatus;
import com.tripiz.api.repository.TripRepository;
import com.tripiz.api.service.BusPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Controller
public class BusPositionResource {

    private final BusPositionService busPositionService;
    private final TripRepository tripRepository;

    @MessageMapping("/bus/join")
    @SendTo("/topic/positions")
    public BusPosition join(
            @Payload BusPosition position,
            SimpMessageHeaderAccessor headerAccessor) {

        UUID busId = resolveBusIdFromDriver(position);
        position.setBusId(busId);

        headerAccessor.getSessionAttributes().put("busId", busId);

        position.setType(PositionMessageType.JOIN);
        busPositionService.savePosition(position);

        return position;
    }

    @MessageMapping("/bus/update")
    @SendTo("/topic/positions")
    public BusPosition updatePosition(
            @Payload BusPosition position,
            SimpMessageHeaderAccessor headerAccessor) {

        Map<String, Object> sessionAttrs = headerAccessor.getSessionAttributes();
        UUID busId = (sessionAttrs != null) ? (UUID) sessionAttrs.get("busId") : null;

        if (busId == null) {
            busId = resolveBusIdFromDriver(position);
            if (sessionAttrs != null) {
                sessionAttrs.put("busId", busId);
            }
        }

        position.setBusId(busId);
        position.setType(PositionMessageType.UPDATE);
        busPositionService.savePosition(position);

        return position;
    }

    private UUID resolveBusIdFromDriver(BusPosition position) {
        if (position.getDriverId() == null) {
            throw new IllegalStateException("Aucun driverId fourni dans le payload");
        }

        Trip trajetEnCours = tripRepository.findByDriverIdAndTripStatus(position.getDriverId(), TripStatus.EN_COURS)
                .orElseThrow(() -> new IllegalStateException("Aucun trajet en cours pour ce chauffeur : " + position.getDriverId()));

        return trajetEnCours.getBusId();
    }

    @GetMapping("/bus/{id}/position")
    public ResponseEntity<BusPosition> getPosition(
            @PathVariable UUID id) {

        BusPosition position = busPositionService.getPosition(id);

        if (position == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(position);
    }

    @GetMapping("/bus/positions")
    public ResponseEntity<List<BusPosition>> getAllPositions() {
        return ResponseEntity.ok(busPositionService.getAllPositions());
    }
}