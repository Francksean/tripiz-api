package com.tripiz.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trip")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "trip_id")
    private UUID tripId;

    @Column(name = "bus_id")
    private UUID busId;

    @Column(name = "driver_id")
    private UUID driverId;

    @Column(name = "itinerary_id")
    private UUID itineraryId;

    @Column(name = "trip_date")
    private LocalDate tripDate;

    @Column(name = "schedule_departure")
    private LocalTime scheduleDeparture;

    @Column(name = "actual_departure")
    private LocalTime actualDeparture;

    @Column(name = "trip_status")
    private String tripStatus;

    @Column(name = "passenger_count")
    private Integer passengerCount;
}



