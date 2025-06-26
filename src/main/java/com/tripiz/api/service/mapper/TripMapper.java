package com.tripiz.api.service.mapper;


import com.tripiz.api.domain.Trip;
import com.tripiz.api.model.TripDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TripMapper {
    TripDTO toDTO(Trip trip);
    List<TripDTO> toDTOList(List<Trip> trips);
}
