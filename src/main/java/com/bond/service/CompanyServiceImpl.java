package com.bond.service;

import com.bond.dto.CompanyResponseDto;
import com.bond.dto.CreateCompanyRequestDto;
import com.bond.mapper.CompanyMapper;
import com.bond.model.Company;
import com.bond.repository.CompanyRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyMapper companyMapper;
    private final CompanyRepository companyRepository;

    @Override
    public List<CompanyResponseDto> getAll(Pageable pageable) {
        return companyRepository.findAll(pageable)
                .stream()
                .map(companyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponseDto create(CreateCompanyRequestDto requestDto) {
        Company company = companyMapper.toModel(requestDto);
        return companyMapper.toDto(companyRepository.save(company));
    }
}
