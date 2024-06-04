package com.bond.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "reports")
@SQLDelete(sql = "UPDATE reports SET is_deleted = TRUE WHERE id = ?")
@Getter
@Setter
@Where(clause = "is_deleted = false")
public class Report {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID",
            strategy = "com.bond.generator.UuidGenerator")
    private String id;

    @Column(nullable = false)
    private Long companyId;

    @Column(nullable = false)
    private LocalDateTime reportDate;

    @Column(nullable = false)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private BigDecimal netProfit;

    private boolean isDeleted = false;
}
