package com.bond.service;

import com.bond.dto.user.UserRegistrationRequestDto;
import com.bond.dto.user.UserResponseDto;

public interface UserService {

    UserResponseDto register(UserRegistrationRequestDto requestDto);
}
