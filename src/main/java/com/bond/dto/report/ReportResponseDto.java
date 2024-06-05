package com.bond.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReportResponseDto {
    private UUID id;
    private UUID companyId;
    private LocalDateTime reportDate;
    private BigDecimal totalRevenue;
    private BigDecimal netProfit;
}
