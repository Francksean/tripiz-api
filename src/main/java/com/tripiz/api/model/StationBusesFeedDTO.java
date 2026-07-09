package com.tripiz.api.model;

import java.util.List;
import java.util.UUID;

public record StationBusesFeedDTO(
        UUID stationId,
        List<StationBusDTO> buses
) {}