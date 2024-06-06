package com.bond.dto.report;

import java.math.BigDecimal;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CreateReportRequestDto {
    @NotNull
    private UUID companyId;
    @NotNull
    private BigDecimal totalRevenue;
    @NotNull
    private BigDecimal netProfit;
}
