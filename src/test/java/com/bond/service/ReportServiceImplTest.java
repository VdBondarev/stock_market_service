package com.bond.service;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bond.dto.report.CreateReportRequestDto;
import com.bond.dto.report.ReportResponseDto;
import com.bond.mapper.ReportDetailsMapper;
import com.bond.mapper.ReportMapper;
import com.bond.model.Company;
import com.bond.model.Report;
import com.bond.model.User;
import com.bond.repository.CompanyRepository;
import com.bond.repository.ReportDetailsRepository;
import com.bond.repository.ReportRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {
    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportMapper reportMapper;

    @Mock
    private ReportDetailsRepository reportDetailsRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ReportDetailsMapper reportDetailsMapper;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    @DisplayName("Verify that getAll() method works as expected")
    public void getAll_ValidInputParams_ReturnsValidList() {
        Report firstReport = createReport(UUID.randomUUID());
        Report secondReport = createReport(UUID.randomUUID());
        Report thirdReport = createReport(UUID.randomUUID());

        List<Report> reports = new ArrayList<>();
        reports.add(firstReport);
        reports.add(secondReport);
        reports.add(thirdReport);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Report> page;
        page = new PageImpl<>(reports, pageable, reports.size());

        ReportResponseDto firstExpectedDto = createResponseDtoFromModel(firstReport);
        ReportResponseDto secondExpectedDto = createResponseDtoFromModel(secondReport);
        ReportResponseDto thirdExpectedDto = createResponseDtoFromModel(thirdReport);

        List<ReportResponseDto> expectedList = new ArrayList<>();
        expectedList.add(firstExpectedDto);
        expectedList.add(secondExpectedDto);
        expectedList.add(thirdExpectedDto);

        when(reportRepository.findAll(pageable)).thenReturn(page);
        when(reportMapper.toResponseDto(firstReport)).thenReturn(firstExpectedDto);
        when(reportMapper.toResponseDto(secondReport)).thenReturn(secondExpectedDto);
        when(reportMapper.toResponseDto(thirdReport)).thenReturn(thirdExpectedDto);

        List<ReportResponseDto> actualList = reportService.getAll(pageable);

        assertEquals(expectedList, actualList);
    }

    @Test
    @DisplayName("Verify that getById() method works as expected with a valid id")
    public void getById_ValidInputParams_ReturnsValidDto() {
        UUID id = UUID.randomUUID();

        Report report = createReport(id);

        ReportResponseDto expectedDto = createResponseDtoFromModel(report);

        when(reportRepository.findById(id)).thenReturn(Optional.of(report));
        when(reportMapper.toResponseDto(report)).thenReturn(expectedDto);

        ReportResponseDto actualDto = reportService.getById(id);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Verify that getById() method throws an exception when passing a non-valid id")
    public void getById_NonValidId_ThrowsException() {
        UUID id = UUID.randomUUID();

        when(reportRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> reportService.getById(id));

        String expectedMessage = "Report with id " + id + " not found";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Verify that create() method works as expected with valid input params")
    public void create_ValidInputParams_ReturnsValidDto() {
        UUID reportId = UUID.randomUUID();
        CreateReportRequestDto requestDto = new CreateReportRequestDto();
        requestDto.setCompanyId(reportId);
        requestDto.setNetProfit(BigDecimal.TEN);
        requestDto.setTotalRevenue(BigDecimal.TEN);

        UUID companyId = UUID.randomUUID();
        Company company = createCompany(companyId);

        User user = new User();
        user.setId(1L);

        Report report = createReportFromRequestDto(requestDto);
        report.setId(UUID.randomUUID());

        ReportResponseDto expectedDto = createResponseDtoFromModel(report);

        when(companyRepository.findById(requestDto.getCompanyId()))
                .thenReturn(Optional.of(company));
        when(reportMapper.toModel(requestDto)).thenReturn(report);
        when(reportRepository.save(report)).thenReturn(report);
        when(reportDetailsRepository.save(any())).thenReturn(any());
        when(reportMapper.toResponseDto(report)).thenReturn(expectedDto);

        ReportResponseDto actualResponseDto = reportService.create(requestDto, user);

        assertEquals(expectedDto, actualResponseDto);
    }

    @Test
    @DisplayName(
            "Verify that create() method throws an exception "
                    + "when a non-existing company's id is passed"
    )
    public void create_NonExistingCompanyId_ThrowsException() {
        UUID companyId = UUID.randomUUID();

        CreateReportRequestDto requestDto = new CreateReportRequestDto();
        requestDto.setCompanyId(companyId);

        User user = new User();

        when(companyRepository.findById(requestDto.getCompanyId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> reportService.create(requestDto, user));

        String expectedMessage = "Company with id " + companyId + " not found";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName(
            "Verify that create() method throws an exception when "
                    + "user is not an admin or tries to create a report for not their company"
    )
    public void create_NotAllowedToInteractUser_ThrowsException() {
        UUID companyId = UUID.randomUUID();
        CreateReportRequestDto requestDto = new CreateReportRequestDto();
        requestDto.setCompanyId(companyId);

        Company company = createCompany(UUID.randomUUID());

        // user is not an owner of the company
        User user = new User();
        user.setId(12215215L);
        user.setRoles(new HashSet<>());

        when(companyRepository.findById(requestDto.getCompanyId()))
                .thenReturn(Optional.of(company));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reportService.create(requestDto, user));

        String expectedMessage = "You are not allowed to create a report for this company";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    private Report createReportFromRequestDto(CreateReportRequestDto requestDto) {
        return new Report()
                .setCompanyId(requestDto.getCompanyId())
                .setNetProfit(requestDto.getNetProfit())
                .setTotalRevenue(requestDto.getTotalRevenue());
    }

    private Company createCompany(UUID id) {
        return new Company()
                .setId(id)
                .setName("test")
                .setCreatedAt(now())
                .setAddress("test")
                .setRegistrationNumber("test")
                .setOwnerId(1L);
    }

    private ReportResponseDto createResponseDtoFromModel(Report report) {
        return new ReportResponseDto()
                .setId(report.getId())
                .setCompanyId(report.getCompanyId())
                .setReportDate(report.getReportDate())
                .setNetProfit(report.getNetProfit())
                .setTotalRevenue(report.getTotalRevenue());
    }

    private Report createReport(UUID id) {
        return new Report()
                .setReportDate(now())
                .setId(id)
                .setCompanyId(UUID.randomUUID())
                .setTotalRevenue(BigDecimal.TEN)
                .setNetProfit(BigDecimal.TEN);
    }
}
