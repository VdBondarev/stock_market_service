package com.bond.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportResponseDto {
    private UUID id;
    private UUID companyId;
    private LocalDateTime reportDate;
    private BigDecimal totalRevenue;
    private BigDecimal netProfit;
}
