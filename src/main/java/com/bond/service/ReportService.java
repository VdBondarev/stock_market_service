package com.bond.service;

import com.bond.dto.report.ReportResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ReportService {

    List<ReportResponseDto> getAll(Pageable pageable);
}
