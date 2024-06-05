package com.bond.service;

import static java.time.LocalDateTime.now;

import com.bond.dto.report.CreateReportRequestDto;
import com.bond.dto.report.ReportResponseDto;
import com.bond.dto.report.UpdateReportRequestDto;
import com.bond.dto.report.details.ReportDetailsResponseDto;
import com.bond.mapper.ReportDetailsMapper;
import com.bond.mapper.ReportMapper;
import com.bond.model.Company;
import com.bond.model.Report;
import com.bond.model.ReportDetails;
import com.bond.model.User;
import com.bond.model.data.FinancialData;
import com.bond.repository.CompanyRepository;
import com.bond.repository.ReportDetailsRepository;
import com.bond.repository.ReportRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private static final int TWO = 2;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final ReportDetailsRepository reportDetailsRepository;
    private final CompanyRepository companyRepository;
    private final ReportDetailsMapper reportDetailsMapper;

    @Override
    public List<ReportResponseDto> getAll(Pageable pageable) {
        return reportRepository
                .findAll(pageable)
                .stream()
                .map(reportMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReportResponseDto getById(UUID id) {
        return reportRepository.findById(id)
                .map(reportMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Report with id " + id + " not found"
                ));
    }

    @Override
    @Transactional
    public ReportResponseDto create(CreateReportRequestDto requestDto, User user) {
        Company company = companyRepository
                .findById(requestDto.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Company with id " + requestDto.getCompanyId() + " not found")
                );
        if (!allowedToInteract(company, user)) {
            throw new IllegalArgumentException(
                    "You are not allowed to create a report for this company"
            );
        }
        Report report = reportMapper.toModel(requestDto);
        report.setReportDate(now());
        reportRepository.save(report);
        ReportDetails reportDetails = createReportDetails(report, ReportDetails.Type.CREATE);
        reportDetailsRepository.save(reportDetails);
        return reportMapper.toResponseDto(report);
    }

    @Override
    public void delete(UUID id) {
        reportRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ReportResponseDto update(UUID reportId, UpdateReportRequestDto requestDto) {
        Report report = reportRepository
                .findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Report with id " + reportId + " not found")
                );
        report = reportMapper.updateModel(report, requestDto);
        reportRepository.save(report);
        ReportDetails reportDetails = createReportDetails(report, ReportDetails.Type.UPDATE);
        reportDetailsRepository.save(reportDetails);
        return reportMapper.toResponseDto(report);
    }

    @Override
    public List<ReportResponseDto> getAllReportsForCompany(UUID companyId, Pageable pageable) {
        return reportRepository.findAllByCompanyId(companyId, pageable)
                .stream()
                .map(reportMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReportDetailsResponseDto getReportDetails(UUID reportId) {
        ReportDetails reportDetails = reportDetailsRepository.findByReportId(reportId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Report details for a report with id " + reportId + " not found")
                );
        return reportDetailsMapper.toResponseDto(reportDetails);
    }

    private boolean allowedToInteract(Company company, User user) {
        return company.getOwnerId().equals(user.getId()) || user.getRoles().size() == TWO;
    }

    private ReportDetails createReportDetails(Report report, ReportDetails.Type type) {
        FinancialData financialData = new FinancialData()
                .setNetProfit(report.getNetProfit())
                .setTotalRevenue(report.getTotalRevenue());
        if (report.getTotalRevenue().equals(BigDecimal.ZERO)) {
            financialData.setNetProfitMargin(BigDecimal.ZERO);
        } else {
            financialData.setNetProfitMargin(report.getNetProfit()
                    .divide(
                            report.getTotalRevenue(), RoundingMode.HALF_UP)
            );
        }
        return new ReportDetails()
                .setFinancialData(financialData)
                .setReportId(report.getId())
                .setComments(
                "Report for company with id "
                        + report.getCompanyId()
                        + " created "
                        + report.getReportDate()
        )
                .setType(type);
    }
}
