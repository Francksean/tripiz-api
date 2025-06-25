package com.tripiz.api.service.mapper;

import com.tripiz.api.domain.User;
import com.tripiz.api.model.SignupResponseDTO;
import com.tripiz.api.model.UpdateUserRequestDTO;
import com.tripiz.api.model.UserDTO;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface UserMapper {

    SignupResponseDTO toSignupResponseDTO(User user);

    @Mapping(target = "createdAt", expression = "java(formatDate(user.getCreatedAt()))")
    UserDTO toUserDTO(User user);
    default String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UpdateUserRequestDTO dto, @MappingTarget User user);

}
