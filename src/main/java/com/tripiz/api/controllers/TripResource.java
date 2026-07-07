package com.tripiz.api.controllers;


import com.tripiz.api.domain.Trip;
import com.tripiz.api.model.*;
import com.tripiz.api.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trip/admin")
@RequiredArgsConstructor
public class TripResource {

    private final TripService tripService;

    @PostMapping("/createTrip")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> createTrip(@RequestBody CreateTripRequestDTO request) {
        tripService.createTrip(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getTripsByDriver")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<TripDTO>> getTripsByDriver() {
        List<TripDTO> trips = tripService.getTripsByRandomDriver();

        if (trips.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(trips);
    }

    @PatchMapping("/patch/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> updateTrip(@PathVariable UUID id, @RequestBody CreateTripRequestDTO request) {
        tripService.updateTrip(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteTrip(@PathVariable UUID id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/countAllPassengers")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Integer> countAllPassengers() {
        int count = tripService.countAllPassengers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/countAllTrips")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Integer> countAllTrips() {
        int count = tripService.countAllTrips();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<Trip>> getAllTrips() {
        List<Trip> trip = tripService.getAllTrips();
        return ResponseEntity.ok(trip);
    }

    @GetMapping("/getStatistics")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<TripStatisticsDTO> getStatistics() {
        TripStatisticsDTO trip = tripService.getStatistics();
        return ResponseEntity.ok(trip);
    }

}
