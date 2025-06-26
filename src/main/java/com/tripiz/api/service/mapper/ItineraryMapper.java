package com.tripiz.api.service.mapper;

import com.tripiz.api.domain.Itinerary;
import com.tripiz.api.model.ItineraryResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItineraryMapper {

    ItineraryResponseDTO toItineraryResponseDTO(Itinerary itinerary);
}
