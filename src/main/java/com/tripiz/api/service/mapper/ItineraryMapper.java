package com.tripiz.api.service.mapper;

import com.tripiz.api.domain.Itinerary;
import com.tripiz.api.model.CreateItineraryRequestDTO;
import com.tripiz.api.model.ItineraryAdminDTO;
import com.tripiz.api.model.ItineraryResponseDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItineraryMapper {

    // Mapping public : on ignore les champs station (on les remplit manuellement)
    @Mapping(target = "departureStation", ignore = true)
    @Mapping(target = "arrivalStation", ignore = true)
    ItineraryResponseDTO toDTO(Itinerary itinerary);

    List<ItineraryResponseDTO> toDTOList(List<Itinerary> itineraries);

    @Mapping(source = "itineraryId", target = "itinerary_id")
    @Mapping(source = "routeName", target = "route_name")
    @Mapping(source = "itineraryName", target = "itinerary_name")
    @Mapping(source = "estimatedDuration", target = "estimated_duration")
    @Mapping(source = "departureStation", target = "departure_station")
    @Mapping(source = "arrivalStation", target = "arrival_station")
    @Mapping(source = "distance", target = "distance")
    @Mapping(source = "ticketPrice", target = "ticket_price")
    ItineraryAdminDTO toAdminDTO(Itinerary itinerary);

    List<ItineraryAdminDTO> toAdminDTOList(List<Itinerary> itineraries);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItineraryFromDto(CreateItineraryRequestDTO dto, @MappingTarget Itinerary itinerary);
}