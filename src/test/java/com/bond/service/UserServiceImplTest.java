package com.bond.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bond.dto.user.UserRegistrationRequestDto;
import com.bond.dto.user.UserResponseDto;
import com.bond.exception.RegistrationException;
import com.bond.mapper.UserMapper;
import com.bond.model.Role;
import com.bond.model.User;
import com.bond.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final String COMPANY_OWNER = "COMPANY_OWNER";
    private static final String ADMIN = "ADMIN";
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Verify that register() method works as expected for valid input params")
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
    @DisplayName("Verify that register() method works as expected for already registered email")
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

    @Test
    @DisplayName("Verify that updateRole() method works as expected when updating owner to owner")
    void updateRole_AlreadyOwner_ReturnsNothingUpdated() {
        User user = createUser(1L);

        UserResponseDto expected = createResponseDto(user);

        when(userRepository.findByIdWithRoles(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expected);

        UserResponseDto actual =
                userService.updateRole(user.getId(), COMPANY_OWNER);

        assertEquals(expected, actual);

        // verifying that update has not been performed
        verify(userRepository, times(0)).save(user);
    }

    @Test
    @DisplayName("Verify that updateRole() method works as expected when updating admin to owner")
    void updateRole_UpdateAdminToOwner_ReturnsUpdatedUser() {
        Role userRole = new Role(1L);
        userRole.setName(Role.RoleName.ROLE_COMPANY_OWNER);

        Role adminRole = new Role(2L);
        adminRole.setName(Role.RoleName.ROLE_ADMIN);

        // now this user is an admin
        User user = createUser(1L);
        HashSet<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);
        user.setRoles(roles);

        UserResponseDto expected = createResponseDto(user);

        when(userRepository.findByIdWithRoles(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(expected);

        UserResponseDto actual =
                userService.updateRole(user.getId(), COMPANY_OWNER);

        assertEquals(expected, actual);

        // verifying that update has been performed
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Verify that updateRole() method works as expected when updating owner to admin")
    void updateRole_UpdateOwnerToAdmin_ReturnsUpdatedUser() {
        User user = createUser(1L);

        // expecting that user will be admin after updating
        UserResponseDto expected = createResponseDto(user);

        when(userRepository.findByIdWithRoles(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(expected);

        UserResponseDto actual =
                userService.updateRole(user.getId(), ADMIN);

        assertEquals(expected, actual);

        // verifying that update has been performed
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Verify that updateRole() method works as expected when updating admin to admin")
    public void updateRole_UpdateAdminToAdmin_ReturnsNothingUpdated() {
        User user = createUser(1L);
        // now this user is admin
        user.getRoles().add(new Role(2L));

        UserResponseDto expected = createResponseDto(user);

        when(userRepository.findByIdWithRoles(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.updateRole(user.getId(), ADMIN);

        assertEquals(expected, actual);

        // verifying that update has not been performed
        verify(userRepository, times(0)).save(user);
    }

    @Test
    @DisplayName("Verify that getById() method works as expected with a valid input")
    public void getById_ValidInput_ReturnsUser() {
        Long userId = 1L;

        User user = createUser(1L);

        UserResponseDto expected = createResponseDto(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.getById(userId);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify that getById() method works as expected with non-valid input")
    public void getById_NonValidInput_ThrowsException() {
        Long nonValidId = 1251255236L;

        when(userRepository.findById(nonValidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> userService.getById(nonValidId)
        );

        String expectedMessage = "User with id " + nonValidId + " not found";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Verify that getAll() method works as expected")
    public void getAll_ValidPageable_ReturnsAllUsers() {
        // just mocker users
        User firstUser = createUser(1L);
        User secondUser = createUser(2L);
        User thirdUser = createUser(3L);

        List<User> users = new ArrayList<>();
        users.add(firstUser);
        users.add(secondUser);
        users.add(thirdUser);

        Pageable pageable = PageRequest.of(0, 5);

        Page<User> expectedPage;
        expectedPage = new PageImpl<>(users, pageable,users.size());

        UserResponseDto firstResponseDto = createResponseDto(firstUser);
        UserResponseDto secondResponseDto = createResponseDto(secondUser);
        UserResponseDto thirdResponseDto = createResponseDto(thirdUser);

        List<UserResponseDto> expectedList = new ArrayList<>();
        expectedList.add(firstResponseDto);
        expectedList.add(secondResponseDto);
        expectedList.add(thirdResponseDto);

        when(userRepository.findAll(pageable)).thenReturn(expectedPage);
        when(userMapper.toResponseDto(firstUser)).thenReturn(firstResponseDto);
        when(userMapper.toResponseDto(secondUser)).thenReturn(secondResponseDto);
        when(userMapper.toResponseDto(thirdUser)).thenReturn(thirdResponseDto);

        List<UserResponseDto> actualList = userService.getAll(pageable);

        assertEquals(expectedList, actualList);
    }

    private User createUser(Long id) {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L));
        return new User()
                .setId(id)
                .setEmail("testemail@example.com")
                .setPassword("testpassword")
                .setLastName("Test")
                .setFirstName("Test")
                .setRoles(roles);
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
