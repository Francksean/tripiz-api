package com.tripiz.api.common.service.mapper;

import com.tripiz.api.common.domain.User;
import com.tripiz.api.model.SignupResponseDTO;
import com.tripiz.api.model.UpdateUserRequestDTO;
import com.tripiz.api.model.UserDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    SignupResponseDTO toSignupResponseDTO(User user);
    UserDTO toUserDTO(User user);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UpdateUserRequestDTO dto, @MappingTarget User user);

}
