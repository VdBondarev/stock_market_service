package com.bond.model.data;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialData {
    private BigDecimal totalRevenue;

    private BigDecimal netProfit;
}
