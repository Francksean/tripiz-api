package com.tripiz.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripWithItineraryDetailsDTO {
    private UUID tripId;
    private UUID busId;
    private UUID driverId;
    private LocalDate tripDate;
    private LocalTime scheduleDeparture;
    private LocalTime actualDeparture;
    private String tripStatus;
    private Integer passengerCount;
    private ItineraryWithStationsDTO itinerary;
}