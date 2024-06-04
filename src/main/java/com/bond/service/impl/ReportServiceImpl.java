package com.bond.service.impl;

import static java.time.LocalDateTime.now;

import com.bond.dto.report.CreateReportRequestDto;
import com.bond.dto.report.ReportResponseDto;
import com.bond.mapper.ReportMapper;
import com.bond.model.Company;
import com.bond.model.Report;
import com.bond.model.ReportDetails;
import com.bond.model.User;
import com.bond.model.data.FinancialData;
import com.bond.repository.CompanyRepository;
import com.bond.repository.ReportDetailsRepository;
import com.bond.repository.ReportRepository;
import com.bond.service.ReportService;
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
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final ReportDetailsRepository reportDetailsRepository;
    private final CompanyRepository companyRepository;

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
        // a check if a company exists
        Company company = companyRepository.findById(requestDto.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Company with id "
                                + requestDto.getCompanyId()
                                + " not found. Report is not created")
                );
        if (!company.getOwnerId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "You cannot create a report for this company. You are not its owner"
            );
        }
        Report report = reportMapper.toModel(requestDto);
        report.setReportDate(now());
        reportRepository.save(report);
        ReportDetails reportDetails = createReportDetails(report);
        reportDetailsRepository.save(reportDetails);
        return reportMapper.toResponseDto(report);
    }

    private ReportDetails createReportDetails(Report report) {
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
        );
    }
}
