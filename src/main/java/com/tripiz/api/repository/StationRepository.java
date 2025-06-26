package com.tripiz.api.repository;

import com.tripiz.api.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StationRepository extends JpaRepository<Station, UUID> {

    boolean existsByStationName(String stationName);

    @Query("SELECT s FROM Station s WHERE s.latitude BETWEEN :minLat AND :maxLat AND s.longitude BETWEEN :minLng AND :maxLng")
    List<Station> findAllWithinSquare(@Param("minLat") double minLat,
                                      @Param("maxLat") double maxLat,
                                      @Param("minLng") double minLng,
                                      @Param("maxLng") double maxLng);


    long countByStatusIgnoreCase(String status);
}
