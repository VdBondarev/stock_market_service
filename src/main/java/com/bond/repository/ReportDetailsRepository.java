package com.bond.repository;

import com.bond.model.ReportDetails;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportDetailsRepository extends MongoRepository<ReportDetails, UUID> {

    Optional<ReportDetails> findByReportId(UUID reportId);
}
