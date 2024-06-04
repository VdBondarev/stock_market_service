package com.bond.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportResponseDto {
    private String id;
    private Long companyId;
    private LocalDateTime reportDate;
    private BigDecimal totalRevenue;
    private BigDecimal netProfit;
}
