package com.tripiz.api.controllers;


import com.tripiz.api.model.CreateStationRequestDTO;
import com.tripiz.api.model.CreateTripRequestDTO;
import com.tripiz.api.model.TripDTO;
import com.tripiz.api.service.StationService;
import com.tripiz.api.service.TripService;
import com.tripiz.api.service.mapper.StationMapper;
import com.tripiz.api.service.mapper.TripMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
public class TripResource {

    private final TripService tripService;

    @PostMapping("/createTrip")
    public ResponseEntity<Void> createTrip(@RequestBody CreateTripRequestDTO request) {
        tripService.createTrip(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getTripsByDriver")
    public ResponseEntity<List<TripDTO>> getTripsByDriver() {
        List<TripDTO> trips = tripService.getTripsByRandomDriver();

        if (trips.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(trips);
    }


}
