package com.tripiz.api.service;

import com.tripiz.api.domain.Bus;
import com.tripiz.api.domain.Direction;
import com.tripiz.api.domain.Itinerary;
import com.tripiz.api.domain.Station;
import com.tripiz.api.model.CreateItineraryRequestDTO;
import com.tripiz.api.model.ItineraryResponseDTO;
import com.tripiz.api.repository.ItineraryRepository;
import com.tripiz.api.service.mapper.ItineraryMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ItineraryService {

    public final ItineraryRepository itineraryRepository;
    public final ItineraryMapper itineraryMapper;

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

    public ItineraryResponseDTO getItineraryById(UUID id) {
        return itineraryRepository.findById(id)
                .map(itineraryMapper::toItineraryResponseDTO)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));
    }

    public List<Itinerary> getItinerariesByDepartureStation(UUID stationId) {
        return itineraryRepository.findByDepartureStation(stationId);
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

    public List<ItineraryResponseDTO> getAllItineraries() {
        List<Itinerary> itinerary = itineraryRepository.findAll();

        if (itinerary.isEmpty()) {
            return Collections.emptyList();
        }
        return itinerary.stream().map(itineraryMapper::toDTO).toList();
    }
}
