package com.tripiz.api.controllers;

import com.tripiz.api.domain.BusPosition;
import com.tripiz.api.domain.PositionMessageType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BusPositionController {

    @MessageMapping("/bus/join")
    @SendTo("/topic/positions")
    public BusPosition join(@Payload BusPosition position, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("busId", position.getBusId());
        position.setType(PositionMessageType.JOIN);
        log.info(" Bus joined: {}", position);
        return position;
    }

    @MessageMapping("/bus/update")
    @SendTo("/topic/positions")
    public BusPosition updatePosition(@Payload BusPosition position) {
        position.setType(PositionMessageType.UPDATE);
        log.info("🛰 Bus updated: {}", position);
        return position;
    }
}
