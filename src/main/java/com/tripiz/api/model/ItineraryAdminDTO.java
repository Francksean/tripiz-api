package com.tripiz.api.model;

import lombok.Data;
import java.util.UUID;

@Data
public class ItineraryAdminDTO {
    private UUID itinerary_id;
    private String route_name;
    private String direction;
    private String itinerary_name;
    private int estimated_duration;
    private UUID departure_station;
    private UUID arrival_station;
    private double distance;
}