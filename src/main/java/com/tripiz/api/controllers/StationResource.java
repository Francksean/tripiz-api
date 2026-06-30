package com.tripiz.api.controllers;


import com.tripiz.api.domain.Station;
import com.tripiz.api.model.CreateStationRequestDTO;
import com.tripiz.api.model.GetStationsRequestDTO;
import com.tripiz.api.model.StationDTO;
import com.tripiz.api.service.StationService;
import com.tripiz.api.service.mapper.StationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/station")
@RequiredArgsConstructor
public class StationResource {

    private final StationService stationService;
    private final StationMapper stationMapper;

    @PostMapping("/createStation")
    public ResponseEntity<Void> createStation(@RequestBody CreateStationRequestDTO request) {
        stationService.createStation(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/stations")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<StationDTO>> getAllStations() {
        List<StationDTO> station = stationService.getAllStations();
        return ResponseEntity.ok(station);
    }

    @PatchMapping("/admin/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> updateStation(@PathVariable UUID id, @RequestBody CreateStationRequestDTO request) {
        stationService.updateStation(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable UUID id) {
        stationService.deleteStation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/countInServiceStation")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Integer> countInServiceStation() {
        int count = stationService.countInServiceStation();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/admin/countInMaintenanceStation")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Integer> countInMaintenanceStation() {
        int count = stationService.countInMaintenanceStation();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/admin/countAllStations")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Integer> countAllStations() {
        int count = stationService.countAllStations();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/stations/within-square")
    public ResponseEntity<List<StationDTO>> getStationsInSquare(@RequestBody GetStationsRequestDTO bounds) {
        List<StationDTO> stations = stationService.findAllStationsWithinSquare(bounds);
        return ResponseEntity.ok(stations);
    }


}
