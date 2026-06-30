package com.tripiz.api.model;

import lombok.Data;

@Data
public class GetStationsRequestDTO {
    private Double minLat;
    private Double maxLat;
    private Double minLng;
    private Double maxLng;
}