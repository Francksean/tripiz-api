package com.tripiz.api.repository;

import com.tripiz.api.domain.Trip;
import com.tripiz.api.domain.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {

    boolean existsByDriverIdAndBusIdAndItineraryId(UUID driverId, UUID busId, UUID itineraryId);

    List<Trip> findAllByDriverId(UUID driverId);

    @Query("SELECT COALESCE(SUM(t.passengerCount), 0) FROM Trip t")
    int countAllPassengers();

    int countByTripStatus(TripStatus tripStatus);

    long countByTripDateBetween(LocalDate start, LocalDate end);

    Optional<Trip> findByDriverIdAndTripStatus(UUID driverId, TripStatus tripStatus);

    List<Trip> findByItineraryIdAndTripDateAndTripStatusIn(UUID itineraryId, LocalDate aujourdhui, List<TripStatus> enCours);

    List<Trip> findByDriverIdAndTripDate(UUID userId, LocalDate tripDate);

    @Query("SELECT t FROM Trip t WHERE t.itineraryId IN " +
            "(SELECT i.itineraryId FROM Itinerary i WHERE i.departureStation = :stationId OR i.arrivalStation = :stationId)")
    List<Trip> findByStationId(@Param("stationId") UUID stationId);

    @Query("SELECT t FROM Trip t WHERE t.itineraryId IN " +
            "(SELECT i.itineraryId FROM Itinerary i WHERE i.departureStation = :stationId OR i.arrivalStation = :stationId) " +
            "AND t.tripStatus IN :statuses")
    List<Trip> findByStationIdAndStatuses(@Param("stationId") UUID stationId,
                                          @Param("statuses") List<TripStatus> statuses);
}