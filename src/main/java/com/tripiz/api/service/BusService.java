package com.tripiz.api.service;

import com.tripiz.api.domain.Bus;
import com.tripiz.api.domain.Bus;
import com.tripiz.api.domain.User;
import com.tripiz.api.model.*;
import com.tripiz.api.repository.BusRepository;
import com.tripiz.api.service.mapper.BusMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;

    public CreateBusResponseDTO createBus(CreateBusRequestDTO request) {
        if (busRepository.existsByBusNumber(request.getBusNumber())) {
            throw new RuntimeException("Bus already exists");
        }


        Bus bus = Bus.builder()
                .busNumber(request.getBusNumber())
                .matriculation(request.getMatriculation())
                .capacity(request.getCapacity())
                .status(request.getStatus())
                .build();

        Bus savedBus = busRepository.save(bus);
        return busMapper.toCreateBusResponseDTO(savedBus);
    }

    public List<BusDTO> getAllBus() {
        List<Bus> bus = busRepository.findAll();

        if (bus.isEmpty()) {
            return Collections.emptyList();
        }
        return bus.stream().map(busMapper::toBusDTO).toList();
    }

    public int countInServiceBus() {
        return (int) busRepository.countByStatusIgnoreCase("En service");
    }

    public int countInMaintenanceBus() {
        return (int) busRepository.countByStatusIgnoreCase("En maintenance");
    }

    public int countTotalCapacity() {
        Integer sum = busRepository.sumCapacityByStatusIgnoreCase("En service");
        return sum != null ? sum : 0;
    }

    public void deleteBus(UUID id) {
        Bus bus = busRepository.findById(id).orElseThrow(() -> new RuntimeException("Bus not found"));

        busRepository.delete(bus);
    }

    public void updateBus(UUID id, UpdateBusRequestDTO dto) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        busMapper.updateUserFromDto(dto, bus);

        busRepository.save(bus);
    }
}
