package com.bond.mapper;

import com.bond.config.MapperConfig;
import com.bond.dto.CompanyResponseDto;
import com.bond.dto.CreateCompanyRequestDto;
import com.bond.model.Company;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CompanyMapper {

    CompanyResponseDto toDto(Company company);

    Company toModel(CreateCompanyRequestDto requestDto);
}
