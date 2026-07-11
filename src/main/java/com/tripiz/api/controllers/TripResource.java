package com.tripiz.api.controllers;

import com.tripiz.api.model.*;
import com.tripiz.api.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
public class TripResource {

    private final TripService tripService;

    @PostMapping("/admin/createTrip")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> createTrip(@RequestBody CreateTripRequestDTO request) {
        tripService.createTrip(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/getTripsByDriver")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<TripDTO>> getTripsByDriver() {
        List<TripDTO> trips = tripService.getTripsByRandomDriver();

        if (trips.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(trips);
    }

    @GetMapping("/driver/today")
    public ResponseEntity<List<TripDTO>> getTodayTrips(Authentication authentication) {
        return ResponseEntity.ok(
                tripService.getTodayTrips(authentication)
        );
    }

    @PatchMapping("/driver/{tripId}/status")
    public ResponseEntity<Void> updateTripStatus(
            @PathVariable UUID tripId,
            @RequestBody UpdateTripStatusRequestDTO request) {

        tripService.updateTripStatus(tripId, request);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/admin/patch/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> updateTrip(@PathVariable UUID id, @RequestBody CreateTripRequestDTO request) {
        tripService.updateTrip(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteTrip(@PathVariable UUID id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/countAllPassengers")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Integer> countAllPassengers() {
        int count = tripService.countAllPassengers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/admin/countAllTrips")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Integer> countAllTrips() {
        int count = tripService.countAllTrips();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/admin/getAll")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<TripDTO>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @GetMapping("/admin/getStatistics")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<TripStatisticsDTO> getStatistics() {
        TripStatisticsDTO trip = tripService.getStatistics();
        return ResponseEntity.ok(trip);
    }

    @GetMapping("/trip/{tripId}/details")
    public ResponseEntity<TripDetailsDTO> getTripDetails(
            @PathVariable UUID tripId) {

        return ResponseEntity.ok(
                tripService.getTripDetails(tripId)
        );
    }

}
