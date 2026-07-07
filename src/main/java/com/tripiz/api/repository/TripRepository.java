package com.tripiz.api.repository;

import com.tripiz.api.domain.Trip;
import com.tripiz.api.domain.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {

    boolean existsByDriverIdAndBusIdAndItineraryId(UUID driverId, UUID busId, UUID itineraryId);
    List<Trip> findAllByDriverId(UUID driverId);
    @Query("SELECT COALESCE(SUM(t.passengerCount), 0) FROM Trip t")
    int countAllPassengers();

    int countByTripStatus(TripStatus tripStatus);

    long countByTripDateBetween(LocalDateTime start, LocalDateTime end);

}
