package com.bond.repository;

import com.bond.model.Report;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, UUID> {

    List<Report> findAllByCompanyId(UUID companyId, Pageable pageable);
}
