package com.bond.mapper;

import com.bond.config.MapperConfig;
import com.bond.dto.company.CompanyResponseDto;
import com.bond.dto.company.CompanyUpdateRequestDto;
import com.bond.dto.company.CreateCompanyRequestDto;
import com.bond.model.Company;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CompanyMapper {

    CompanyResponseDto toResponseDto(Company company);

    Company toModel(CreateCompanyRequestDto requestDto);

    void updateModel(@MappingTarget Company company, CompanyUpdateRequestDto requestDto);
}
