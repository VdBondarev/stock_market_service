package com.bond.dto.report.details;

import com.bond.model.ReportDetails;
import com.bond.model.data.FinancialData;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDetailsResponseDto {
    private UUID reportId;
    private FinancialData financialData;
    private String comments;
    private ReportDetails.Type type;
}
