package com.tripiz.api.repository;

import com.tripiz.api.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {

    boolean existsByDriverIdAndBusIdAndItineraryId(UUID driverId, UUID busId, UUID itineraryId);

}
