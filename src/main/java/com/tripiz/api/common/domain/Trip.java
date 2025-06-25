package com.tripiz.api.common.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "itinerary_id")
    private UUID itineraryId;

    @Column(name = "trip_date")
    private Date tripDate;

    @Column(name = "schedule_departure")
    private String scheduleDeparture;

    @Column(name = "actual_departure")
    private String actualDeparture;

    @Column(name = "trip_status")
    private String tripStatus;

    @Column(name = "passenger_count")
    private Integer passengerCount;
}



