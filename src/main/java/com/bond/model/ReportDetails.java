package com.bond.model;

import com.bond.model.data.FinancialData;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "report_details")
@Getter
@Setter
@Accessors(chain = true)
public class ReportDetails {
    @Id
    @Field(name = "report_id")
    private UUID reportId;

    @Field("financial_data")
    private FinancialData financialData;

    private String comments;

    private Type type;

    private boolean deleted = false;

    public enum Type {
        CREATE,
        UPDATE
    }
}
