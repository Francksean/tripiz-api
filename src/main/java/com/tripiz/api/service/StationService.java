package com.tripiz.api.service;

import com.tripiz.api.domain.Station;
import com.tripiz.api.model.CreateStationRequestDTO;
import com.tripiz.api.model.GetStationsRequestDTO;
import com.tripiz.api.model.StationDTO;
import com.tripiz.api.repository.StationRepository;
import com.tripiz.api.service.mapper.StationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    public void createStation(CreateStationRequestDTO request) {
        if (stationRepository.existsByStationName(request.getStationName())) {
            throw new RuntimeException("Station already exists");
        }


        Station station = Station.builder()
                .stationName(request.getStationName())
                .stationType(request.getStationType())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(request.getStatus())
                .build();

        Station savedStation = stationRepository.save(station);
    }

    public List<StationDTO> getAllStations() {
        List<Station> station = stationRepository.findAll();

        if (station.isEmpty()) {
            return Collections.emptyList();
        }
        return station.stream().map(stationMapper::toDTO).toList();
    }

    public void updateStation(UUID id, CreateStationRequestDTO request) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        stationMapper.updateStationFromDto(request, station);

        stationRepository.save(station);
    }

    public void deleteStation(UUID id) {
       Station station = stationRepository.findById(id).orElseThrow(() -> new RuntimeException("Station not found"));

       stationRepository.delete(station);
    }

    public int countInServiceStation() {
        return (int) stationRepository.countByStatusIgnoreCase("En service");
    }

    public int countInMaintenanceStation() {
        return (int) stationRepository.countByStatusIgnoreCase("En maintenance");
    }

    public int countAllStations() {
        return (int) stationRepository.count();
    }

    public List<StationDTO> findAllStationsWithinSquare(GetStationsRequestDTO request) {
        List<Station> stations = stationRepository.findAllWithinSquare(
                request.getMinLat(),
                request.getMaxLat(),
                request.getMinLng(),
                request.getMaxLng()
        );

        return stationMapper.toDTOList(stations);
    }


}
