package com.bond.service;

import com.bond.dto.user.UserRegistrationRequestDto;
import com.bond.dto.user.UserResponseDto;
import com.bond.exception.RegistrationException;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto getById(Long id);

    List<UserResponseDto> getAll(Pageable pageable);

    UserResponseDto updateRole(Long id, String roleName);
}
