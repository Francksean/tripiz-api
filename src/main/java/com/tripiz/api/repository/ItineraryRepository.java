package com.tripiz.api.repository;

import com.tripiz.api.domain.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItineraryRepository extends JpaRepository<Itinerary, UUID> {

}
