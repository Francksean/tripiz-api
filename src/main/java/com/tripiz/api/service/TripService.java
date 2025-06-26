package com.tripiz.api.service;

import com.tripiz.api.domain.Trip;
import com.tripiz.api.model.CreateTripRequestDTO;
import com.tripiz.api.repository.TripRepository;
import com.tripiz.api.service.mapper.TripMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;


@Service
@RequiredArgsConstructor
public class TripService {

    private final TripMapper tripMapper;
    private final TripRepository tripRepository;

    @Transactional
    public void createTrip(CreateTripRequestDTO request) {
        if (tripRepository.existsByDriverIdAndBusIdAndItineraryId(
                request.getDriverId(),
                request.getBusId(),
                request.getItineraryId())) {
            throw new IllegalArgumentException("Ce trajet existe déjà");
        }

        Trip trip = Trip.builder()
                .busId(request.getBusId())
                .driverId(request.getDriverId())
                .itineraryId(request.getItineraryId())
                .tripDate(request.getTripDate())
                .scheduleDeparture(LocalTime.parse(request.getScheduleDeparture()))
                .actualDeparture(LocalTime.parse(request.getActualDeparture()))
                .build();

        tripRepository.save(trip);
    }

}
