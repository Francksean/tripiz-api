package com.tripiz.api.controllers;


import com.tripiz.api.model.*;
import com.tripiz.api.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bus")
@RequiredArgsConstructor
public class BusResource {

    private final BusService busService;

    @PostMapping("/createBus")
    public ResponseEntity<CreateBusResponseDTO> createBus(@RequestBody CreateBusRequestDTO request) {
        CreateBusResponseDTO response = busService.createBus(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/buses")
    public ResponseEntity<List<BusDTO>> getAllBus() {
        List<BusDTO> user = busService.getAllBus();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin/countInServiceBus")
    public ResponseEntity<Integer> countInServiceBus() {
        int count = busService.countInServiceBus();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/admin/countInMaintenanceBus")
    public ResponseEntity<Integer> countInMaintenanceBus() {
        int count = busService.countInMaintenanceBus();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/admin/totalCapacity")
    public ResponseEntity<Integer> getTotalCapacity() {
        return ResponseEntity.ok(busService.countTotalCapacity());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBus(@PathVariable UUID id, @RequestBody UpdateBusRequestDTO updateBusRequestDTO) {
        busService.updateBus(id, updateBusRequestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBus(@PathVariable UUID id) {
        busService.deleteBus(id);
        return ResponseEntity.ok().build();
    }

}
