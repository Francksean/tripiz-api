package com.tripiz.api.service;

import com.tripiz.api.domain.Trip;
import com.tripiz.api.model.CreateTripRequestDTO;
import com.tripiz.api.model.TripDTO;
import com.tripiz.api.repository.TripRepository;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.service.mapper.TripMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TripService {

    private final TripMapper tripMapper;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    @Transactional
    public void createTrip(CreateTripRequestDTO request) {
        if (tripRepository.existsByDriverIdAndBusIdAndItineraryId(
                request.getDriverId(),
                request.getBusId(),
                request.getItineraryId())) {
            throw new IllegalArgumentException("This trip already exists");
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

    public List<TripDTO> getTripsByRandomDriver() {
        List<UUID> driverIds = userRepository.findAllDriverIds();

        if (driverIds.isEmpty()) {
            throw new NoSuchElementException("No driver found");
        }

        UUID randomDriverId = driverIds.get(new Random().nextInt(driverIds.size()));

        List<Trip> trips = tripRepository.findAllByDriverId(randomDriverId);
        return tripMapper.toDTOList(trips);
    }

}
