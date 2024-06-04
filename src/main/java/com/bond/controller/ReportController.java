package com.bond.controller;

import com.bond.dto.report.CreateReportRequestDto;
import com.bond.dto.report.ReportResponseDto;
import com.bond.model.User;
import com.bond.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reports controller", description = "Endpoints for managing reports")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ReportResponseDto create(
            @RequestBody @Valid CreateReportRequestDto requestDto,
            Authentication authentication
    ) {
        return reportService.create(requestDto, getUser(authentication));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Gel all reports with pageable sorting")
    public List<ReportResponseDto> getAll(Pageable pageable) {
        return reportService.getAll(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get a report by id")
    public ReportResponseDto getById(@PathVariable UUID id) {
        return reportService.getById(id);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
