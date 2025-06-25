package com.tripiz.api.controllers;

import com.tripiz.api.domain.Station;
import com.tripiz.api.model.MapBoundsDTO;
import com.tripiz.api.model.StationDTO;
import com.tripiz.api.repository.StationRepository;
import com.tripiz.api.service.mapper.StationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StationMapWSController {

    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    @MessageMapping("/stations/in-area")
    @SendTo("/topic/stations")
    public List<StationDTO> getStationsInArea(@Payload MapBoundsDTO bounds) {
        List<Station> stations = stationRepository.findByMapBounds(
                bounds.getNorth(),
                bounds.getSouth(),
                bounds.getEast(),
                bounds.getWest()
        );

        return stationMapper.toDTOList(stations);

    }
}

