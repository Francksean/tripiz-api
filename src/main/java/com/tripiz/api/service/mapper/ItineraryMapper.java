package com.tripiz.api.service.mapper;

import com.tripiz.api.domain.Itinerary;
import com.tripiz.api.model.CreateItineraryRequestDTO;
import com.tripiz.api.model.ItineraryResponseDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItineraryMapper {

    // 🔥 On ignore les champs station pour le mapping automatique
    @Mapping(target = "departureStation", ignore = true)
    @Mapping(target = "arrivalStation", ignore = true)
    ItineraryResponseDTO toDTO(Itinerary itinerary);

    // Les listes utiliseront la méthode toDTO ci-dessus
    List<ItineraryResponseDTO> toDTOList(List<Itinerary> itineraries);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItineraryFromDto(CreateItineraryRequestDTO dto, @MappingTarget Itinerary itinerary);
}