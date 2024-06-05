package com.bond.controller;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bond.dto.company.CompanyResponseDto;
import com.bond.dto.company.CreateCompanyRequestDto;
import com.bond.holder.LinksHolder;
import com.bond.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompanyControllerTest extends LinksHolder {
    protected static MockMvc mockMvc;
    private static final String BEARER = "Bearer";
    private static final String USER_PASSWORD = "1234567890";
    private static final String USER_EMAIL = "user@example.com";
    private static final String SEPARATOR = " ";
    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String OWNER_ID_FIELD = "ownerId";
    private static final String ID_FIELD = "id";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_COMPANIES_FILE_PATH,
                    INSERT_USER_TO_DATABASE_FILE_PATH,
                    INSERT_COMPANY_OWNER_RELATION_TO_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    REMOVE_ALL_COMPANIES_FILE_PATH,
                    REMOVE_ALL_USER_ROLES_FILE_PATH,
                    REMOVE_ALL_USERS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Verify that create() endpoint works as expected with valid input")
    public void create_ValidInput_Success() throws Exception {
        CreateCompanyRequestDto requestDto = new CreateCompanyRequestDto()
                .setAddress("Address")
                .setName("Name")
                .setRegistrationNumber("RegistrationNumber");

        String content = objectMapper.writeValueAsString(requestDto);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        USER_EMAIL, USER_PASSWORD
                )
        );

        String jwt = jwtUtil.generateToken(authentication.getName());

        MvcResult result = mockMvc.perform(post("/companies")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + SEPARATOR + jwt)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CompanyResponseDto expectedDto = createResponseDtoFromRequest(requestDto);

        CompanyResponseDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), CompanyResponseDto.class
        );

        assertThat(actualDto)
                .usingRecursiveComparison()
                .ignoringFields(ID_FIELD, CREATED_AT_FIELD, OWNER_ID_FIELD)
                .isEqualTo(expectedDto);
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_COMPANIES_FILE_PATH,
                    INSERT_USER_TO_DATABASE_FILE_PATH,
                    INSERT_COMPANY_OWNER_RELATION_TO_USER_ROLES_FILE_PATH,
                    INSERT_COMPANY_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    REMOVE_ALL_COMPANIES_FILE_PATH,
                    REMOVE_ALL_USER_ROLES_FILE_PATH,
                    REMOVE_ALL_USERS_FILE_PATH,
                    REMOVE_ALL_COMPANIES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName(
            "Verify that create() endpoint throws an exception when "
                    + "passed name for creating a company is already taken"
    )
    public void create_ExistingNameIsPassed_Failure() throws Exception {
        CreateCompanyRequestDto requestDto = new CreateCompanyRequestDto()
                .setAddress("Address")
                .setName("Company Name")
                .setRegistrationNumber("RegistrationNumber");

        String content = objectMapper.writeValueAsString(requestDto);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        USER_EMAIL, USER_PASSWORD
                )
        );

        String jwt = jwtUtil.generateToken(authentication.getName());

        MvcResult result = mockMvc.perform(post("/companies")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + SEPARATOR + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String expectedMessage =
                "A company with the specified name or registration number already exists";
        String actualMessage = result.getResolvedException().getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    private CompanyResponseDto createResponseDtoFromRequest(CreateCompanyRequestDto requestDto) {
        return new CompanyResponseDto()
                .setCreatedAt(now())
                .setOwnerId(1L)
                .setId(UUID.randomUUID())
                .setName(requestDto.getName())
                .setRegistrationNumber(requestDto.getRegistrationNumber())
                .setAddress(requestDto.getAddress());
    }
}
