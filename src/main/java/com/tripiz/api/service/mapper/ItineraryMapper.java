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

    @Mapping(target = "departureStation", ignore = true)
    @Mapping(target = "arrivalStation", ignore = true)
    ItineraryResponseDTO toDTO(Itinerary itinerary);

    List<ItineraryResponseDTO> toDTOList(List<Itinerary> itineraries);

    @Mapping(source = "itineraryId", target = "itineraryId")
    @Mapping(source = "routeName", target = "routeName")
    @Mapping(source = "itineraryName", target = "itineraryName")
    @Mapping(source = "estimatedDuration", target = "estimatedDuration")
    @Mapping(source = "departureStation", target = "departureStation")
    @Mapping(source = "arrivalStation", target = "arrivalStation")
    @Mapping(source = "distance", target = "distance")
    @Mapping(source = "ticketPrice", target = "ticketPrice")
    ItineraryAdminDTO toAdminDTO(Itinerary itinerary);

    List<ItineraryAdminDTO> toAdminDTOList(List<Itinerary> itineraries);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItineraryFromDto(CreateItineraryRequestDTO dto, @MappingTarget Itinerary itinerary);


}