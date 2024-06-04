package com.bond.mapper;

import com.bond.config.MapperConfig;
import com.bond.dto.report.details.ReportDetailsResponseDto;
import com.bond.model.ReportDetails;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ReportDetailsMapper {

    ReportDetailsResponseDto toResponseDto(ReportDetails reportDetails);
}
