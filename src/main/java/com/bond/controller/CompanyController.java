package com.bond.controller;

import com.bond.dto.company.CompanyResponseDto;
import com.bond.dto.company.CreateCompanyRequestDto;
import com.bond.service.CompanyService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    public List<CompanyResponseDto> getAll(Pageable pageable) {
        return companyService.getAll(pageable);
    }

    @PostMapping
    public CompanyResponseDto create(
            @RequestBody @Valid CreateCompanyRequestDto requestDto
    ) {
        return companyService.create(requestDto);
    }
}
