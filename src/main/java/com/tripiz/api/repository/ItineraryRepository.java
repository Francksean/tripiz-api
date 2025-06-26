package com.tripiz.api.repository;

import com.tripiz.api.domain.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItineraryRepository extends JpaRepository<Itinerary, UUID> {

    List<Itinerary> findByDepartureStation(UUID stationId);

    boolean existsByItineraryName(String itineraryName);
}
