package com.bond.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bond.dto.user.UserResponseDto;
import com.bond.holder.LinksHolder;
import com.bond.model.User;
import com.bond.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest extends LinksHolder {
    protected static MockMvc mockMvc;
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String ADMIN = "ADMIN";
    private static final int ONE = 1;
    private static final int TWO = 2;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH,
                    REMOVE_ALL_USER_ROLES_FILE_PATH,
                    INSERT_FIVE_USERS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = ADMIN)
    @DisplayName("Verify that getAll() endpoint works as expected")
    public void getAll_ValidInput_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);

        String content = objectMapper.writeValueAsString(pageable);

        MvcResult result = mockMvc.perform(get("/users")
                        .content(content)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto[] returnedResult = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto[].class
        );

        int expectedLength = 5;
        assertEquals(expectedLength, returnedResult.length);
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH,
                    INSERT_USER_TO_DATABASE_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = ADMIN_EMAIL, roles = ADMIN)
    @Test
    @DisplayName("Verify that getById() endpoint works as expected")
    public void getById_ValidInput_Success() throws Exception {
        Long id = 1L;

        MvcResult result = mockMvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto expectedResponse = new UserResponseDto()
                .setEmail("user@example.com")
                .setId(1L)
                .setFirstName("User")
                .setLastName("User");

        UserResponseDto actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class
        );

        assertEquals(expectedResponse, actualResponse);
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH,
                    REMOVE_ALL_USER_ROLES_FILE_PATH,
                    INSERT_USER_TO_DATABASE_FILE_PATH,
                    INSERT_COMPANY_OWNER_RELATION_TO_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH,
                    REMOVE_ALL_USERS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Verify that updateRole() endpoint works as expected "
            + "when updating a company owner to an admin")
    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = ADMIN)
    void updateRole_CompanyOwnerToAdmin_Success() throws Exception {
        Long id = 1L;

        mockMvc.perform(
                        patch("/users/" + id + "/role")
                                .param("role", ADMIN)
                )
                .andExpect(status().isOk());

        // expecting that if get() throws NoSuchElementException, test will fail
        User user = userRepository.findByIdWithRoles(id).get();
        assertEquals(TWO, user.getRoles().size());
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH,
                    REMOVE_ALL_USER_ROLES_FILE_PATH,
                    INSERT_USER_TO_DATABASE_FILE_PATH,
                    INSERT_ADMIN_RELATION_TO_USER_ROLES_FILE_PATH
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
    @WithMockUser(username = ADMIN_EMAIL, roles = ADMIN)
    @DisplayName(
            "Verify that updateRole() endpoint works as expected "
                    + "when updating an admin to a company owner"
    )
    public void updateRole_AdminToCompanyOwner_Success() throws Exception {
        String roleName = "COMPANY_OWNER";

        Long id = 1L;

        mockMvc.perform(
                        patch("/users/" + id + "/role")
                                .param("role", roleName)
                )
                .andExpect(status().isOk());

        // expecting that if get() throws NoSuchElementException, test will fail
        User user = userRepository.findByIdWithRoles(id).get();
        assertEquals(ONE, user.getRoles().size());
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH,
                    REMOVE_ALL_USER_ROLES_FILE_PATH,
                    INSERT_USER_TO_DATABASE_FILE_PATH,
                    INSERT_ADMIN_RELATION_TO_USER_ROLES_FILE_PATH
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
    @WithMockUser(username = ADMIN_EMAIL, roles = ADMIN)
    @DisplayName(
            "Verify that updateRole() endpoint works as expected "
                    + "when updating an admin to an admin"
    )
    public void updateRole_AdminToAdmin_NothingUpdated()
            throws Exception {

        Long id = 1L;

        mockMvc.perform(
                        patch("/users/" + id + "/role")
                                .param("role", ADMIN)
                )
                .andExpect(status().isOk());

        // expecting that if get() throws NoSuchElementException, test will fail
        User user = userRepository.findByIdWithRoles(id).get();
        assertEquals(TWO, user.getRoles().size());
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH,
                    REMOVE_ALL_USER_ROLES_FILE_PATH,
                    INSERT_USER_TO_DATABASE_FILE_PATH,
                    INSERT_COMPANY_OWNER_RELATION_TO_USER_ROLES_FILE_PATH
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
    @WithMockUser(username = ADMIN_EMAIL, roles = ADMIN)
    @DisplayName(
            "Verify that updateRole() endpoint works as expected "
                    + "when updating a company owner to a company owner"
    )
    public void updateRole_CompanyOwnerToCompanyOwner_NothingUpdated()
            throws Exception {
        String roleName = "COMPANY_OWNER";

        Long id = 1L;

        mockMvc.perform(
                        patch("/users/" + id + "/role")
                                .param("role", roleName)
                )
                .andExpect(status().isOk());

        // expecting that if get() throws NoSuchElementException, test will fail
        User user = userRepository.findByIdWithRoles(id).get();
        assertEquals(ONE, user.getRoles().size());
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH,
                    INSERT_USER_TO_DATABASE_FILE_PATH,
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    REMOVE_ALL_USERS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = ADMIN)
    @DisplayName("Verify that delete() endpoint works as expected")
    public void delete_ValidId_Success() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/users/" + id))
                .andExpect(status().isNoContent())
                .andReturn();

        Page<User> users = userRepository.findAll(PageRequest.of(0, 5));
        long expectedSize = 0;
        long actualSize = users.getTotalElements();

        assertEquals(expectedSize, actualSize);
    }
}
