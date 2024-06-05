package com.bond.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.bond.dto.user.UserRegistrationRequestDto;
import com.bond.dto.user.UserResponseDto;
import com.bond.exception.RegistrationException;
import com.bond.mapper.UserMapper;
import com.bond.model.Role;
import com.bond.model.User;
import com.bond.repository.UserRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Verify that registration works fine for valid input params")
    void register_ValidRequest_RegistersUser() throws RegistrationException {
        UserRegistrationRequestDto requestDto =
                createRegistrationRequestDto("test@gmail.com", "testPassword");

        User user = createUser(requestDto);

        UserResponseDto expected = createResponseDto(user);

        when(userRepository.findByEmailWithoutRoles(requestDto.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(requestDto.getPassword());
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.register(requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify that registration works as expected for already registered email")
    void register_AlreadyRegisteredEmail_ThrowsException() {
        UserRegistrationRequestDto requestDto =
                createRegistrationRequestDto("test@gmail.com", "testPassword");

        User user = createUser(requestDto);

        when(userRepository.findByEmailWithoutRoles(requestDto.getEmail()))
                .thenReturn(Optional.of(user));

        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> userService.register(requestDto));

        String expected = "User with passed email already exists. Pick another one";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    private User createUser(UserRegistrationRequestDto requestDto) {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L));
        return new User()
                .setId(1L)
                .setPassword(requestDto.getPassword())
                .setRoles(roles)
                .setEmail(requestDto.getEmail())
                .setLastName(requestDto.getLastName())
                .setFirstName(requestDto.getFirstName());
    }

    private UserResponseDto createResponseDto(User user) {
        return new UserResponseDto()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName());
    }

    private UserRegistrationRequestDto createRegistrationRequestDto(String email, String password) {
        return new UserRegistrationRequestDto()
                .setFirstName("Firstname")
                .setLastName("Lastname")
                .setEmail(email)
                .setPassword(password)
                .setRepeatPassword(password);
    }
}
