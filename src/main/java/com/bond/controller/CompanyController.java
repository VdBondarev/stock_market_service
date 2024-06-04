package com.bond.controller;

import com.bond.dto.company.CompanyResponseDto;
import com.bond.dto.company.CompanyUpdateRequestDto;
import com.bond.dto.company.CreateCompanyRequestDto;
import com.bond.model.User;
import com.bond.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Company controller", description = "Endpoints for managing companies")
@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    @Operation(summary = "Create a company")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponseDto create(
            @RequestBody @Valid CreateCompanyRequestDto requestDto,
            Authentication authentication
    ) {
        return companyService.create(requestDto, getUser(authentication));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a company by id")
    public CompanyResponseDto getById(@PathVariable String id) {
        return companyService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Get all companies with pageable sorting")
    public List<CompanyResponseDto> getAll(Pageable pageable) {
        return companyService.getAll(pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update info about a company",
            description = "Allowed for owners of the company or admins only. "
                    + "You are allowed to update name of the company or address only. "
                    + "If name already exists, update will not be performed")
    public CompanyResponseDto update(
            @PathVariable String id,
            @RequestBody CompanyUpdateRequestDto requestDto,
            Authentication authentication
    ) {
        return companyService.update(id, requestDto, getUser(authentication));
    }

    @GetMapping("/mine")
    @Operation(summary = "Get user's companies")
    public List<CompanyResponseDto> getMine(
            Authentication authentication,
            Pageable pageable
    ) {
        return companyService.getMine(getUser(authentication), pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a company by id",
            description = "Allowed for admins only")
    public void deleteById(@PathVariable String id) {
        companyService.deleteById(id);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
