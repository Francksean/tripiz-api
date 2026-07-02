package com.tripiz.api.controllers;

import com.tripiz.api.domain.BusPosition;
import com.tripiz.api.domain.PositionMessageType;
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
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Controller
public class BusPositionController {

    private final BusPositionService busPositionService;

    @MessageMapping("/bus/join")
    @SendTo("/topic/positions")
    public BusPosition join(
            @Payload BusPosition position,
            SimpMessageHeaderAccessor headerAccessor) {

        headerAccessor.getSessionAttributes().put("busId", position.getBusId());

        position.setType(PositionMessageType.JOIN);

        busPositionService.savePosition(position);

        return position;
    }

    @MessageMapping("/bus/update")
    @SendTo("/topic/positions")
    public BusPosition updatePosition(@Payload BusPosition position) {

        position.setType(PositionMessageType.UPDATE);

        busPositionService.savePosition(position);

        return position;
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