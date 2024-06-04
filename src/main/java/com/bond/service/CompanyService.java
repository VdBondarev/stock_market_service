package com.bond.service;

import com.bond.dto.company.CompanyResponseDto;
import com.bond.dto.company.CompanyUpdateRequestDto;
import com.bond.dto.company.CreateCompanyRequestDto;
import com.bond.model.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface CompanyService {

    List<CompanyResponseDto> getAll(Pageable pageable);

    CompanyResponseDto create(CreateCompanyRequestDto requestDto, User user);

    CompanyResponseDto getById(UUID id);

    void deleteById(UUID id);

    CompanyResponseDto update(UUID id, CompanyUpdateRequestDto requestDto, User user);

    List<CompanyResponseDto> getMine(User user, Pageable pageable);
}
