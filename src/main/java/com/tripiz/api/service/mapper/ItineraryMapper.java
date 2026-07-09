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

    // Mapping pour le DTO public avec objets StationDTO
    @Mapping(target = "departureStation", ignore = true)
    @Mapping(target = "arrivalStation", ignore = true)
    ItineraryResponseDTO toDTO(Itinerary itinerary);

    List<ItineraryResponseDTO> toDTOList(List<Itinerary> itineraries);

    // Nouveau mapping pour le DTO admin avec IDs
    ItineraryAdminDTO toAdminDTO(Itinerary itinerary);

    List<ItineraryAdminDTO> toAdminDTOList(List<Itinerary> itineraries);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItineraryFromDto(CreateItineraryRequestDTO dto, @MappingTarget Itinerary itinerary);
}