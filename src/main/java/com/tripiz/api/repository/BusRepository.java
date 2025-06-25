package com.tripiz.api.repository;

import com.tripiz.api.domain.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BusRepository extends JpaRepository<Bus, UUID> {
    boolean existsByBusNumber(Integer busNumber);

    long countByStatusIgnoreCase(String status);

    @Query("SELECT SUM(b.capacity) FROM Bus b WHERE LOWER(b.status) = LOWER(:status)")
    Integer sumCapacityByStatusIgnoreCase(@Param("status") String status);

}
