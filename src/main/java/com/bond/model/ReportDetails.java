package com.bond.model;

import com.bond.model.data.FinancialData;
import javax.persistence.Column;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "report_details")
public class ReportDetails {

    @Column(nullable = false)
    private String reportId;

    @Column(nullable = false)
    private FinancialData financialData;
}
