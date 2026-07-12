package com.tripiz.api.service;

import com.tripiz.api.domain.*;
import com.tripiz.api.domain.TripStatus;
import com.tripiz.api.model.*;
import com.tripiz.api.repository.ItineraryRepository;
import com.tripiz.api.repository.StationRepository;
import com.tripiz.api.repository.TripRepository;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.service.mapper.ItineraryMapper;
import com.tripiz.api.service.mapper.StationMapper;
import com.tripiz.api.service.mapper.TripMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripMapper tripMapper;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final ItineraryRepository itineraryRepository;
    private final ItineraryMapper itineraryMapper;
    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

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
            trip.setScheduleDeparture(LocalTime.parse(request.getScheduleDeparture()));
        }
        if (request.getTripStatus() != null) {
            trip.setTripStatus(TripStatus.valueOf(request.getTripStatus().name()));
        }
        if (request.getActualDeparture() != null) {
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

    public List<TripDTO> getAllTrips() {
        List<Trip> trips = tripRepository.findAll();
        return tripMapper.toDTOList(trips);
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

    public List<TripDTO> getTodayTrips(Authentication authentication) {

        String keycloakId = authentication.getName();

        User driver = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        List<Trip> trips = tripRepository.findByDriverIdAndTripDate(
                driver.getUserId(),
                LocalDate.now()
        );

        Set<UUID> itineraryIds = trips.stream()
                .map(Trip::getItineraryId)
                .collect(Collectors.toSet());

        Map<UUID, Itinerary> itinerairesParId = itineraryRepository.findAllById(itineraryIds)
                .stream()
                .collect(Collectors.toMap(Itinerary::getItineraryId, i -> i));

        return trips.stream()
                .map(trip -> {
                    TripDTO dto = tripMapper.toDTO(trip);

                    Itinerary itinerary = itinerairesParId.get(trip.getItineraryId());

                    if (itinerary != null) {
                        dto.setItinerary(
                                itineraryMapper.toAdminDTO(itinerary)
                        );
                    }

                    return dto;
                })
                .toList();
    }

    public void updateTripStatus(UUID tripId,
                                 UpdateTripStatusRequestDTO request) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        trip.setTripStatus(TripStatus.valueOf(request.getTripStatus().name()));

        tripRepository.save(trip);
    }

    public TripDetailsDTO getTripDetails(UUID tripId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        Itinerary itinerary = itineraryRepository.findById(trip.getItineraryId())
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));

        Station departureStation = stationRepository
                .findById(itinerary.getDepartureStation())
                .orElseThrow(() -> new RuntimeException("Departure station not found"));

        Station arrivalStation = stationRepository
                .findById(itinerary.getArrivalStation())
                .orElseThrow(() -> new RuntimeException("Arrival station not found"));

        TripDTO tripDTO = tripMapper.toDTO(trip);

        ItineraryWithStationsDTO itineraryDTO =
                new ItineraryWithStationsDTO();

        itineraryDTO.setItineraryId(itinerary.getItineraryId());
        itineraryDTO.setRouteName(itinerary.getRouteName());
        itineraryDTO.setDirection(itinerary.getDirection().name());
        itineraryDTO.setItineraryName(itinerary.getItineraryName());
        itineraryDTO.setEstimatedDuration(itinerary.getEstimatedDuration());
        itineraryDTO.setDistance(itinerary.getDistance());

        itineraryDTO.setDepartureStation(
                stationMapper.toDTO(departureStation)
        );

        itineraryDTO.setArrivalStation(
                stationMapper.toDTO(arrivalStation)
        );

        TripDetailsDTO response = new TripDetailsDTO();
        response.setTrip(tripDTO);
        response.setItinerary(itineraryDTO);

        return response;
    }

    public List<TripWithItineraryDetailsDTO> getTripsByStation(UUID stationId) {
        stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found with id: " + stationId));

        List<Trip> trips = tripRepository.findByStationId(stationId);

        if (trips.isEmpty()) {
            return new ArrayList<>();
        }

        return buildTripsWithItineraryDetails(trips);
    }

    public List<TripWithItineraryDetailsDTO> getTripsByStationAndStatuses(
            UUID stationId,
            List<TripStatus> statuses) {

        stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found with id: " + stationId));

        List<Trip> trips = tripRepository.findByStationIdAndStatuses(stationId, statuses);

        if (trips.isEmpty()) {
            return new ArrayList<>();
        }

        return buildTripsWithItineraryDetails(trips);
    }

    private List<TripWithItineraryDetailsDTO> buildTripsWithItineraryDetails(List<Trip> trips) {
        Set<UUID> itineraryIds = trips.stream()
                .map(Trip::getItineraryId)
                .collect(Collectors.toSet());

        Map<UUID, Itinerary> itineraryMap = itineraryRepository.findAllById(itineraryIds)
                .stream()
                .collect(Collectors.toMap(Itinerary::getItineraryId, i -> i));

        Set<UUID> stationIds = itineraryMap.values().stream()
                .flatMap(itinerary -> Stream.of(
                        itinerary.getDepartureStation(),
                        itinerary.getArrivalStation()
                ))
                .collect(Collectors.toSet());

        Map<UUID, Station> stationMap = stationRepository.findAllById(stationIds)
                .stream()
                .collect(Collectors.toMap(Station::getStationId, s -> s));

        return trips.stream()
                .map(trip -> {
                    Itinerary itinerary = itineraryMap.get(trip.getItineraryId());
                    if (itinerary == null) {
                        return null;
                    }

                    Station departureStation = stationMap.get(itinerary.getDepartureStation());
                    Station arrivalStation = stationMap.get(itinerary.getArrivalStation());

                    ItineraryWithStationsDTO itineraryDTO = new ItineraryWithStationsDTO();
                    itineraryDTO.setItineraryId(itinerary.getItineraryId());
                    itineraryDTO.setRouteName(itinerary.getRouteName());
                    itineraryDTO.setDirection(itinerary.getDirection().name());
                    itineraryDTO.setItineraryName(itinerary.getItineraryName());
                    itineraryDTO.setEstimatedDuration(itinerary.getEstimatedDuration());
                    itineraryDTO.setDistance(itinerary.getDistance());

                    if (departureStation != null) {
                        itineraryDTO.setDepartureStation(stationMapper.toDTO(departureStation));
                    }
                    if (arrivalStation != null) {
                        itineraryDTO.setArrivalStation(stationMapper.toDTO(arrivalStation));
                    }

                    TripWithItineraryDetailsDTO tripDTO = new TripWithItineraryDetailsDTO();
                    tripDTO.setTripId(trip.getTripId());
                    tripDTO.setBusId(trip.getBusId());
                    tripDTO.setDriverId(trip.getDriverId());
                    tripDTO.setTripDate(trip.getTripDate());
                    tripDTO.setScheduleDeparture(trip.getScheduleDeparture());
                    tripDTO.setActualDeparture(trip.getActualDeparture());
                    tripDTO.setTripStatus(trip.getTripStatus() != null ? trip.getTripStatus().name() : null);
                    tripDTO.setPassengerCount(trip.getPassengerCount());
                    tripDTO.setItinerary(itineraryDTO);

                    return tripDTO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}