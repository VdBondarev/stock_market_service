package com.bond.mapper;

import com.bond.config.MapperConfig;
import com.bond.dto.user.UserRegistrationRequestDto;
import com.bond.dto.user.UserResponseDto;
import com.bond.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    User toModel(UserRegistrationRequestDto requestDto);

    UserResponseDto toResponseDto(User user);
}
