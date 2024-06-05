package com.bond.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bond.dto.user.UserLoginRequestDto;
import com.bond.dto.user.UserLoginResponseDto;
import com.bond.dto.user.UserRegistrationRequestDto;
import com.bond.dto.user.UserResponseDto;
import com.bond.holder.LinksHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest extends LinksHolder {
    protected static MockMvc mockMvc;
    private static final String JWT_REGEX =
            "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$";
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Sql(
            scripts = { REMOVE_ALL_USERS_FILE_PATH },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = { REMOVE_ALL_USERS_FILE_PATH },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Verify that registration endpoint works as expected with valid input")
    @Test
    void register_ValidInput_Success() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail("test@test.com")
                .setPassword("12345678")
                .setRepeatPassword("12345678")
                .setLastName("Test")
                .setFirstName("Test");

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/auth/registration")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), UserResponseDto.class);

        UserResponseDto expected = createUserResponseDto(1L, requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName(
            "Verify that registration endpoint works as expected when passing a registered email"
    )
    @Sql(
            scripts = {INSERT_USER_TO_DATABASE_FILE_PATH},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = { REMOVE_ALL_USERS_FILE_PATH },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void register_AlreadyExistingEmailIsPassed_Failure() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail("admin@example.com")
                .setLastName("Test")
                .setFirstName("Test")
                .setPassword("12345678")
                .setRepeatPassword("12345678");

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/auth/registration")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String expectedMessage = "User with passed email already exists. Pick another one";

        String actualMessage = result.getResolvedException().getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName(
            "Verify that registration endpoint works as expected when passing non-valid params"
    )
    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH,
                    INSERT_USER_TO_DATABASE_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void register_NonValidParam_Failure() throws Exception {
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto(
                "non-valid",
                "non-valid",
                "non-valid",
                "1234",
                "1234"
        );

        String content = objectMapper.writeValueAsString(requestDto);

        // expecting that input validation will work and will not let a request go further
        mockMvc.perform(
                        post("/auth/registration")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH,
                    INSERT_USER_TO_DATABASE_FILE_PATH,
                    REMOVE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH,
                    REMOVE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Verify that login() endpoint works as expected")
    void login_ValidInput_Success() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto();
        requestDto.setPassword("1234567890");
        requestDto.setEmail("admin@example.com");

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(get("/auth/login")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserLoginResponseDto responseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserLoginResponseDto.class
        );

        assertTrue(responseDto.getToken().matches(JWT_REGEX));
    }

    private UserRegistrationRequestDto createUserRegistrationRequestDto(
            String firstName,
            String lastName,
            String email,
            String password,
            String repeatPassword
    ) {
        return new UserRegistrationRequestDto()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setRepeatPassword(repeatPassword)
                .setPassword(password);
    }

    private UserResponseDto createUserResponseDto(Long id, UserRegistrationRequestDto requestDto) {
        return new UserResponseDto()
                .setId(id)
                .setEmail(requestDto.getEmail())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName());
    }
}
