package com.tripiz.api.repository;

import com.tripiz.api.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StationRepository extends JpaRepository<Station, UUID> {

    boolean existsByStationName(String stationName);

    @Query("SELECT s FROM Station s WHERE s.latitude BETWEEN :south AND :north AND s.longitude BETWEEN :west AND :east")
    List<Station> findByMapBounds(
            @Param("north") double north,
            @Param("south") double south,
            @Param("east") double east,
            @Param("west") double west
    );

    long countByStatusIgnoreCase(String status);
}
