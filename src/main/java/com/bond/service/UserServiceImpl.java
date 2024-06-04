package com.bond.service;

import com.bond.dto.user.UserRegistrationRequestDto;
import com.bond.dto.user.UserResponseDto;
import com.bond.exception.RegistrationException;
import com.bond.mapper.UserMapper;
import com.bond.model.Role;
import com.bond.model.User;
import com.bond.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmailWithoutRoles(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException(
                    "User with passed email already exists. Pick another one");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L));
        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }
}
