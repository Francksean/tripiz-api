package com.tripiz.api.service.mapper;


import com.tripiz.api.domain.Station;
import com.tripiz.api.model.CreateStationRequestDTO;
import com.tripiz.api.model.StationDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StationMapper {

    StationDTO toDTO(Station station);

    List<StationDTO> toDTOList(List<Station> stations);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStationFromDto(CreateStationRequestDTO dto, @MappingTarget Station station);
}
