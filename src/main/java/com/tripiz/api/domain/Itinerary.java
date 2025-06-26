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
    @Column(name = "itineray_id")
    public UUID itineraryId;

    @Column(name = "route_id")
    public UUID routeId;

    @Column(name = "direction")
    public Direction direction;

    @Column(name = "itineray_name")
    public String itineraryName;

    @Column(name = "estimated_duration")
    public int estimatedDuration;

    @Column(name = "departure_station")
    public UUID departureStation;

    @Column(name = "arrival_station")
    public UUID arrivalStation;

    @Column(name = "distance")
    public double distance;
}
