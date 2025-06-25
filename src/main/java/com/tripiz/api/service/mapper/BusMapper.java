package com.tripiz.api.service.mapper;


import com.tripiz.api.domain.Bus;
import com.tripiz.api.model.BusDTO;
import com.tripiz.api.model.CreateBusResponseDTO;
import com.tripiz.api.model.UpdateBusRequestDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BusMapper {

    CreateBusResponseDTO toCreateBusResponseDTO(Bus bus);
    BusDTO toBusDTO(Bus bus);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UpdateBusRequestDTO dto, @MappingTarget Bus bus);
}
