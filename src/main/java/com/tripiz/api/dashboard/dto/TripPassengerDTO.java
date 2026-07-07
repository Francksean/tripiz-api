package com.tripiz.api.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TripPassengerDTO {
    private UUID tripId;
    private String tripName; // si vous avez un champ de nom
    private long passengerCount;
}
