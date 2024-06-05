package com.bond.mapper;

import com.bond.config.MapperConfig;
import com.bond.dto.report.CreateReportRequestDto;
import com.bond.dto.report.ReportResponseDto;
import com.bond.dto.report.UpdateReportRequestDto;
import com.bond.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface ReportMapper {

    ReportResponseDto toResponseDto(Report report);

    Report toModel(CreateReportRequestDto requestDto);

    Report updateModel(@MappingTarget Report report, UpdateReportRequestDto requestDto);
}
