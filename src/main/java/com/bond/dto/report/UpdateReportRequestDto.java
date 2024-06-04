package com.bond.dto.report;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReportRequestDto {
    private BigDecimal totalRevenue;
    private BigDecimal netProfit;
}
