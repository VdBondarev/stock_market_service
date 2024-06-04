package com.bond.model.data;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FinancialData {
    private BigDecimal totalRevenue;
    private BigDecimal netProfit;
    private BigDecimal netProfitMargin;
}
