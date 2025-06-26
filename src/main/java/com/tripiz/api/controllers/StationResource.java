package com.tripiz.api.controllers;


import com.tripiz.api.domain.Station;
import com.tripiz.api.model.CreateStationRequestDTO;
import com.tripiz.api.model.GetStationsRequestDTO;
import com.tripiz.api.model.StationDTO;
import com.tripiz.api.service.StationService;
import com.tripiz.api.service.mapper.StationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<StationDTO>> getAllStations() {
        List<StationDTO> station = stationService.getAllStations();
        return ResponseEntity.ok(station);
    }

    @PatchMapping("/admin/{id}")
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
    public ResponseEntity<Integer> countInServiceStation() {
        int count = stationService.countInServiceStation();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/admin/countInMaintenanceStation")
    public ResponseEntity<Integer> countInMaintenanceStation() {
        int count = stationService.countInMaintenanceStation();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/admin/countAllStations")
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
