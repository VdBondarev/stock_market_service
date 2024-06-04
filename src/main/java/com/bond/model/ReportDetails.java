package com.bond.model;

import com.bond.model.data.FinancialData;
import java.util.UUID;
import javax.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "report_details")
@Getter
@Setter
@Accessors(chain = true)
public class ReportDetails {
    @Column(nullable = false)
    private UUID reportId;

    @Column(nullable = false)
    private FinancialData financialData;

    private String comments;
}
