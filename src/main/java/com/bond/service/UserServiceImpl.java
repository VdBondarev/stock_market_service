package com.bond.service;

import com.bond.dto.user.UserRegistrationRequestDto;
import com.bond.dto.user.UserResponseDto;
import com.bond.exception.RegistrationException;
import com.bond.mapper.UserMapper;
import com.bond.model.Role;
import com.bond.model.User;
import com.bond.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int ONE = 1;
    private static final int TWO = 2;
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

    @Override
    public UserResponseDto getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id " + id + " not found")
                );
    }

    @Override
    public List<UserResponseDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto update(Long id, String roleName) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with id " + id + " not found")
                );
        Role.RoleName role = Role.RoleName.fromString(roleName);
        if (userIs(user, Role.RoleName.ROLE_COMPANY_OWNER)
                && role.equals(Role.RoleName.ROLE_COMPANY_OWNER)) {
            return userMapper.toResponseDto(user);
        }
        if (userIs(user, Role.RoleName.ROLE_ADMIN)
                && role.equals(Role.RoleName.ROLE_ADMIN)) {
            return userMapper.toResponseDto(user);
        }
        if (userIs(user, Role.RoleName.ROLE_COMPANY_OWNER)
                && role.equals(Role.RoleName.ROLE_ADMIN)) {
            user.getRoles().add(new Role(2L));
        } else {
            Set<Role> roles = new HashSet<>();
            roles.add(new Role(1L));
            user.setRoles(roles);
        }
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    private boolean userIs(User user, Role.RoleName roleName) {
        if (roleName.equals(Role.RoleName.ROLE_COMPANY_OWNER)) {
            return user.getRoles().size() == ONE;
        }
        return user.getRoles().size() == TWO;
    }
}
