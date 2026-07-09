package com.tripiz.api.model;

import com.tripiz.api.domain.Direction;
import java.util.UUID;

public record StationBusDTO(
        UUID busId,
        String routeName,
        Direction direction,
        UUID itineraryId,
        int etaSeconds,
        double latitude,
        double longitude,
        Double heading
) {}