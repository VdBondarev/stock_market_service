package com.bond.service.impl;

import com.bond.dto.report.ReportResponseDto;
import com.bond.service.ReportService;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {
    @Override
    public List<ReportResponseDto> getAll(Pageable pageable) {
        return List.of();
    }
}
