package com.zimmerh0.medicalclinic.dto.user;

import com.zimmerh0.medicalclinic.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}