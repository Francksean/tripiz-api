package com.tripiz.api.service.mapper;


import com.tripiz.api.domain.Trip;
import com.tripiz.api.model.CreateTripRequestDTO;
import com.tripiz.api.model.TripDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TripMapper {
    TripDTO toDTO(Trip trip);
    List<TripDTO> toDTOList(List<Trip> trips);

    void updateTripFromDto(CreateTripRequestDTO request, @MappingTarget Trip trip);
}
