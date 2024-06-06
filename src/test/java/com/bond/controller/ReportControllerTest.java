package com.bond.controller;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bond.dto.report.ReportResponseDto;
import com.bond.holder.LinksHolder;
import com.bond.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportControllerTest extends LinksHolder {
    private static final String SEPARATOR = " ";
    private static final String AUTHORIZATION = "AUTHORIZATION";
    private static final String BEARER = "Bearer";
    private static final String ADMIN_EMAIL = "user@example.com";
    private static final String COMPANY_OWNER = "COMPANY_OWNER";
    private static final String REPORT_DATE_FIELD = "reportDate";
    private static final String ADMIN_PASSWORD = "1234567890";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Sql(
            scripts = {
                    REMOVE_ALL_REPORTS_FILE_PATH,
                    INSERT_REPORT_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    REMOVE_ALL_REPORTS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Verify that getById() endpoint works as expected")
    @WithMockUser(username = ADMIN_EMAIL, roles = COMPANY_OWNER)
    public void getById_ValidId_Success() throws Exception {
        String id = "123e4567-e89b-12d3-a456-426614174000";

        UUID companyId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        ReportResponseDto expected = new ReportResponseDto()
                .setId(UUID.fromString(id))
                .setCompanyId(companyId)
                .setReportDate(now())
                .setNetProfit(BigDecimal.valueOf(6789.11))
                .setTotalRevenue(BigDecimal.valueOf(12345.67));

        MvcResult result = mockMvc.perform(get("/reports/" + id))
                .andExpect(status().isOk())
                .andReturn();

        ReportResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ReportResponseDto.class
        );

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields(REPORT_DATE_FIELD)
                .isEqualTo(expected);
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_REPORTS_FILE_PATH,
                    INSERT_FIVE_REPORTS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    REMOVE_ALL_REPORTS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Verify that getAllReportsForCompany() endpoint works as expected")
    @WithMockUser(username = ADMIN_EMAIL, roles = COMPANY_OWNER)
    public void getAllReportsForCompany_ValidId_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);

        String content = objectMapper.writeValueAsString(pageable);

        // expecting that this company has 3 reports
        String companyId = "123e4567-e89b-12d3-a456-426614174001";

        MvcResult result = mockMvc.perform(get("/reports/all/" + companyId)
                        .content(content)
                )
                .andExpect(status().isOk())
                .andReturn();

        ReportResponseDto[] actualList = objectMapper.readValue(
                result.getResponse().getContentAsString(), ReportResponseDto[].class
        );

        int expectedLength = 3;

        assertThat(actualList).hasSize(expectedLength);
    }

    @Sql(
            scripts = {
                    REMOVE_ALL_USER_ROLES_FILE_PATH,
                    REMOVE_ALL_USERS_FILE_PATH,
                    REMOVE_ALL_REPORTS_FILE_PATH,
                    INSERT_FIVE_REPORTS_FILE_PATH,
                    INSERT_USER_TO_DATABASE_FILE_PATH,
                    INSERT_ADMIN_RELATION_TO_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    REMOVE_ALL_REPORTS_FILE_PATH,
                    REMOVE_ALL_USERS_FILE_PATH,
                    REMOVE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Verify that getAll() endpoint works as expected")
    public void getAll_ValidId_Success() throws Exception {
        Authentication authentication;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        ADMIN_EMAIL, ADMIN_PASSWORD
                )
        );

        Pageable pageable = PageRequest.of(0, 5);

        String content = objectMapper.writeValueAsString(pageable);

        String jwt = jwtUtil.generateToken(authentication.getName());

        MvcResult result = mockMvc.perform(get("/reports")
                        .principal(authentication)
                        .content(content)
                        .header(AUTHORIZATION, BEARER + SEPARATOR + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

        ReportResponseDto[] actualList = objectMapper.readValue(
                result.getResponse().getContentAsString(), ReportResponseDto[].class
        );

        int expectedLength = 5;

        assertThat(actualList).hasSize(expectedLength);
    }
}
