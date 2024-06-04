package com.bond.repository;

import com.bond.model.Report;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, UUID> {
}
