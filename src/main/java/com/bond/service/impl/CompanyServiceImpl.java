package com.bond.service.impl;

import static java.time.LocalDateTime.now;

import com.bond.dto.company.CompanyResponseDto;
import com.bond.dto.company.CompanyUpdateRequestDto;
import com.bond.dto.company.CreateCompanyRequestDto;
import com.bond.mapper.CompanyMapper;
import com.bond.model.Company;
import com.bond.model.User;
import com.bond.repository.CompanyRepository;
import com.bond.service.CompanyService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    public static final int TWO = 2;
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
    public CompanyResponseDto create(CreateCompanyRequestDto requestDto, User user) {
        if (companyExists(requestDto.getName(), requestDto.getRegistrationNumber())) {
            throw new IllegalArgumentException(
                    "A company with the specified name or registration number already exists"
            );
        }
        Company company = companyMapper.toModel(requestDto);
        company.setCreatedAt(now());
        company.setOwnerId(user.getId());
        return companyMapper.toDto(companyRepository.save(company));
    }

    @Override
    public CompanyResponseDto getById(UUID id) {
        return companyRepository.findById(id)
                .map(companyMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Company with id " + id + " not found"));
    }

    @Override
    public void deleteById(UUID id) {
        companyRepository.deleteById(id);
    }

    @Override
    public CompanyResponseDto update(UUID id, CompanyUpdateRequestDto requestDto, User user) {
        Company company = companyRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Company with id " + id + " not found")
                );
        if (companyRepository.findByName(requestDto.getName()).isPresent()) {
            throw new IllegalArgumentException(
                    "Company with name " + requestDto.getName() + " already exists"
            );
        }
        // check whether the user is the owner or an admin or not
        if (!Objects.equals(company.getOwnerId(), user.getId()) && user.getRoles().size() != TWO) {
            throw new BadCredentialsException(
                    "You do not have permission to update this company"
            );
        }
        if (isValid(requestDto)) {
            companyMapper.updateModel(company, requestDto);
            return companyMapper.toDto(companyRepository.save(company));
        }
        throw new IllegalArgumentException(
                "Update request is not valid. "
                        + "Update must be performed by at least one non-empty field."
        );
    }

    @Override
    public List<CompanyResponseDto> getMine(User user, Pageable pageable) {
        return companyRepository.findAllByOwnerId(user.getId(), pageable)
                .stream()
                .map(companyMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean isValid(CompanyUpdateRequestDto requestDto) {
        return (requestDto.getName() == null || !requestDto.getName().isEmpty())
                && (requestDto.getAddress() == null || !requestDto.getAddress().isEmpty());
    }

    private boolean companyExists(String name, String registrationNumber) {
        return companyRepository.findByName(name).isPresent()
                || companyRepository.findByRegistrationNumber(registrationNumber).isPresent();
    }
}
