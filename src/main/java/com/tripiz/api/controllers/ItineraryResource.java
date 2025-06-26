package com.tripiz.api.controllers;


import com.tripiz.api.domain.Itinerary;
import com.tripiz.api.model.*;
import com.tripiz.api.service.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/itinerary")
@RequiredArgsConstructor
public class ItineraryResource {

    public final ItineraryService itineraryService;

    @PostMapping("/admin/createItinerary")
    public ResponseEntity<Void> createItinerary(@RequestBody CreateItineraryRequestDTO request) {
        itineraryService.createItinerary(request);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<ItineraryResponseDTO> getItineraryById(@PathVariable UUID id) {
        ItineraryResponseDTO response = itineraryService.getItineraryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getStation/{id}")
    public ResponseEntity<List<Itinerary>> getItinerariesByDepartureStation(@PathVariable UUID id) {
        List<Itinerary> response = itineraryService.getItinerariesByDepartureStation(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/{id}")
    public ResponseEntity<Void> updateItinerary(@PathVariable UUID id, @RequestBody CreateItineraryRequestDTO request) {
        itineraryService.updateItinerary(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<Void> deleteItinerary(@PathVariable UUID id) {
        itineraryService.deleteItinerary(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/getAll")
    public ResponseEntity<List<ItineraryResponseDTO>> getAllItineraries() {
        List<ItineraryResponseDTO> station = itineraryService.getAllItineraries();
        return ResponseEntity.ok(station);
    }
}
