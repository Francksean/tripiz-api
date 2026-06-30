package com.tripiz.api.service.mapper;

import com.tripiz.api.domain.User;
import com.tripiz.api.model.SignupResponseDTO;
import com.tripiz.api.model.UpdateUserRequestDTO;
import com.tripiz.api.model.UserDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    SignupResponseDTO toSignupResponseDTO(User user);

    @Mapping(target = "createdAt", dateFormat = "yyyy/MM/dd HH:mm")
    UserDTO toUserDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UpdateUserRequestDTO dto, @MappingTarget User user);
}