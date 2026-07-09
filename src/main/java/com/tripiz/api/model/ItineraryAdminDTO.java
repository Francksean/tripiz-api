package com.tripiz.api.model;

import lombok.Data;
import java.util.UUID;

@Data
public class ItineraryAdminDTO {
    private UUID itineraryId;
    private String routeName;
    private String direction;
    private String itineraryName;
    private int estimatedDuration;
    private UUID departureStation;   // ID de la station de départ
    private UUID arrivalStation;     // ID de la station d'arrivée
    private double distance;
}