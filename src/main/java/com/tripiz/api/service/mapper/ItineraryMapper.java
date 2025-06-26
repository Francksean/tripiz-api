package com.tripiz.api.service.mapper;

import com.tripiz.api.domain.Itinerary;
import com.tripiz.api.domain.User;
import com.tripiz.api.model.CreateItineraryRequestDTO;
import com.tripiz.api.model.ItineraryResponseDTO;
import com.tripiz.api.model.UpdateUserRequestDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ItineraryMapper {

    ItineraryResponseDTO toItineraryResponseDTO(Itinerary itinerary);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItineraryFromDto(CreateItineraryRequestDTO dto, @MappingTarget Itinerary itinerary);

    ItineraryResponseDTO toDTO(Itinerary itinerary);
}
