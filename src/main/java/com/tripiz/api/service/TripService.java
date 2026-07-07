package com.tripiz.api.service;

import com.tripiz.api.domain.Itinerary;
import com.tripiz.api.domain.Trip;
import com.tripiz.api.domain.TripStatus;
import com.tripiz.api.model.CreateTripRequestDTO;
import com.tripiz.api.model.TripDTO;
import com.tripiz.api.model.TripStatisticsDTO;
import com.tripiz.api.repository.TripRepository;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.service.mapper.TripMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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

    public void updateTrip(UUID id, CreateTripRequestDTO request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (request.getBusId() != null) {
            trip.setBusId(request.getBusId());
        }
        if (request.getDriverId() != null) {
            trip.setDriverId(request.getDriverId());
        }
        if (request.getItineraryId() != null) {
            trip.setItineraryId(request.getItineraryId());
        }
        if (request.getTripDate() != null) {
            trip.setTripDate(request.getTripDate());
        }
        if (request.getScheduleDeparture() != null) {
            trip.setActualDeparture(LocalTime.parse(request.getActualDeparture()));
        }
        if (request.getTripStatus() != null) {
            trip.setActualDeparture(LocalTime.parse(request.getActualDeparture()));
        }
        if (request.getPassengerCount() != null) {
            trip.setPassengerCount(request.getPassengerCount());
        }

        tripRepository.save(trip);
    }

    public void deleteTrip(UUID id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));

        tripRepository.delete(trip);
    }

    public int countAllPassengers() {
        return tripRepository.countAllPassengers();
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public int countAllTrips() {
        return (int) tripRepository.count();
    }

    public TripStatisticsDTO getStatistics() {
        return new TripStatisticsDTO()
                .programmed(tripRepository.countByTripStatus(TripStatus.PROGRAMME))
                .ongoing(tripRepository.countByTripStatus(TripStatus.EN_COURS))
                .completed(tripRepository.countByTripStatus(TripStatus.TERMINE))
                .cancelled(tripRepository.countByTripStatus(TripStatus.ANNULE));
    }
}
