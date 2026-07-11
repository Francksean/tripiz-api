package com.tripiz.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "itinerary")
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "itinerary_id")
    private UUID itineraryId;

    @Column(name = "route_name")
    private String routeName;

    @Column(name = "direction")
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column(name = "itinerary_name")
    private String itineraryName;

    @Column(name = "estimated_duration")
    private int estimatedDuration;

    @Column(name = "departure_station")
    private UUID departureStation;

    @Column(name = "arrival_station")
    private UUID arrivalStation;

    @Column(name = "distance")
    private double distance;

    @Column(name = "ticket_price")
    private Double ticketPrice;
}