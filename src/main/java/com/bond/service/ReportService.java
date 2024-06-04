package com.bond.service;

import com.bond.dto.report.CreateReportRequestDto;
import com.bond.dto.report.ReportResponseDto;
import com.bond.model.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ReportService {

    List<ReportResponseDto> getAll(Pageable pageable);

    ReportResponseDto getById(UUID id);

    ReportResponseDto create(CreateReportRequestDto reportService, User user);
}
