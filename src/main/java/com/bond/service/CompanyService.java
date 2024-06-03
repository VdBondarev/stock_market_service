package com.bond.service;

import com.bond.dto.CompanyResponseDto;
import com.bond.dto.CreateCompanyRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CompanyService {

    List<CompanyResponseDto> getAll(Pageable pageable);

    CompanyResponseDto create(CreateCompanyRequestDto requestDto);
}
