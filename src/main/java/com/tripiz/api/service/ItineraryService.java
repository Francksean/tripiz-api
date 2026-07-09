package com.tripiz.api.service;

import com.tripiz.api.domain.Direction;
import com.tripiz.api.domain.Itinerary;
import com.tripiz.api.domain.Station;
import com.tripiz.api.model.CreateItineraryRequestDTO;
import com.tripiz.api.model.ItineraryResponseDTO;
import com.tripiz.api.model.StationDTO;
import com.tripiz.api.repository.ItineraryRepository;
import com.tripiz.api.repository.StationRepository;
import com.tripiz.api.service.mapper.ItineraryMapper;
import com.tripiz.api.service.mapper.StationMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItineraryService {

    public final ItineraryRepository itineraryRepository;
    public final ItineraryMapper itineraryMapper;
    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    @Transactional
    public void createItinerary(CreateItineraryRequestDTO request) {
        if (itineraryRepository.existsByItineraryName(request.getItineraryName())) {
            throw new RuntimeException("Itinerary already exists");
        }

        Itinerary itinerary = Itinerary.builder()
                .itineraryName(request.getItineraryName())
                .routeName(request.getRouteName())
                .direction(Direction.valueOf(request.getDirection().name()))
                .arrivalStation(request.getArrivalStation())
                .departureStation(request.getDepartureStation())
                .distance(request.getDistance())
                .estimatedDuration(request.getEstimatedDuration())
                .build();

       itineraryRepository.save(itinerary);
    }

    public List<ItineraryResponseDTO> getItinerariesByDepartureStation(UUID stationId) {
        List<Itinerary> itineraries = itineraryRepository.findByDepartureStation(stationId);
        if (itineraries.isEmpty()) {
            return Collections.emptyList();
        }

        Station departure = stationRepository.findById(stationId).orElse(null);
        StationDTO departureDTO = departure != null ? stationMapper.toDTO(departure) : null;

        Set<UUID> arrivalIds = itineraries.stream()
                .map(Itinerary::getArrivalStation)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, Station> arrivalMap = stationRepository.findAllById(arrivalIds).stream()
                .collect(Collectors.toMap(Station::getStationId, Function.identity()));

        return itineraries.stream()
                .map(itinerary -> {
                    ItineraryResponseDTO dto = itineraryMapper.toDTO(itinerary);
                    dto.setDepartureStation(departureDTO);
                    Station arrival = arrivalMap.get(itinerary.getArrivalStation());
                    dto.setArrivalStation(arrival != null ? stationMapper.toDTO(arrival) : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ItineraryResponseDTO> getAllItineraries() {
        List<Itinerary> itineraries = itineraryRepository.findAll();
        if (itineraries.isEmpty()) {
            return Collections.emptyList();
        }

        // Récupérer tous les IDs de stations (départ et arrivée)
        Set<UUID> stationIds = new HashSet<>();
        for (Itinerary it : itineraries) {
            if (it.getDepartureStation() != null) stationIds.add(it.getDepartureStation());
            if (it.getArrivalStation() != null) stationIds.add(it.getArrivalStation());
        }
        Map<UUID, Station> stationMap = stationRepository.findAllById(stationIds).stream()
                .collect(Collectors.toMap(Station::getStationId, Function.identity()));

        return itineraries.stream()
                .map(itinerary -> {
                    ItineraryResponseDTO dto = itineraryMapper.toDTO(itinerary);
                    Station dep = stationMap.get(itinerary.getDepartureStation());
                    Station arr = stationMap.get(itinerary.getArrivalStation());
                    dto.setDepartureStation(dep != null ? stationMapper.toDTO(dep) : null);
                    dto.setArrivalStation(arr != null ? stationMapper.toDTO(arr) : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ItineraryResponseDTO getItineraryById(UUID id) {
        Itinerary itinerary = itineraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));
        ItineraryResponseDTO dto = itineraryMapper.toDTO(itinerary);
        // Charger les stations individuellement (ou utiliser un cache)
        Station dep = stationRepository.findById(itinerary.getDepartureStation()).orElse(null);
        Station arr = stationRepository.findById(itinerary.getArrivalStation()).orElse(null);
        dto.setDepartureStation(dep != null ? stationMapper.toDTO(dep) : null);
        dto.setArrivalStation(arr != null ? stationMapper.toDTO(arr) : null);
        return dto;
    }

    public void updateItinerary(UUID id, CreateItineraryRequestDTO request) {
        Itinerary itinerary = itineraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));

        itineraryMapper.updateItineraryFromDto(request, itinerary);

        itineraryRepository.save(itinerary);
    }

    public void deleteItinerary(UUID id) {
        Itinerary itinerary = itineraryRepository.findById(id).orElseThrow(() -> new RuntimeException("Itinerary not found"));

        itineraryRepository.delete(itinerary);
    }

    public List<ItineraryResponseDTO> getAllItinerariesForAdmin() {
        List<Itinerary> itinerary = itineraryRepository.findAll();

        if (itinerary.isEmpty()) {
            return Collections.emptyList();
        }
        return itinerary.stream().map(itineraryMapper::toDTO).toList();
    }
}
