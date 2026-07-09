package com.tripiz.api.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class TripResponseDTO {
    private UUID tripId;
    private UUID busId;
    private UUID driverId;
    private UUID itineraryId;
    private LocalDate tripDate;
    private LocalTime scheduleDeparture;
    private LocalTime actualDeparture;
    private String tripStatus;
    private Integer passengerCount;
}