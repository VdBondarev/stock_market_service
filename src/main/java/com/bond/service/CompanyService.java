package com.bond.service;

import com.bond.dto.company.CompanyResponseDto;
import com.bond.dto.company.CompanyUpdateRequestDto;
import com.bond.dto.company.CreateCompanyRequestDto;
import com.bond.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CompanyService {

    List<CompanyResponseDto> getAll(Pageable pageable);

    CompanyResponseDto create(CreateCompanyRequestDto requestDto, User user);

    CompanyResponseDto getById(String id);

    void deleteById(String id);

    CompanyResponseDto update(String id, CompanyUpdateRequestDto requestDto, User user);

    List<CompanyResponseDto> getMine(User user, Pageable pageable);
}
