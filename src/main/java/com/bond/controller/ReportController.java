package com.bond.controller;

import com.bond.dto.report.ReportResponseDto;
import com.bond.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reports controller", description = "Endpoints for managing reports")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping
    public List<ReportResponseDto> getAll(Pageable pageable) {
        return reportService.getAll(pageable);
    }
}
